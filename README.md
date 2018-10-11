# spark-json-rules-engine
## *An approach to apply query rules on deep nested jsons*

- The project demonstrates an approach on how one can build a rules engine which executes its rules on parts of a json
- One can provide a nested json like below sample and the project would flatten it into a dataframe
	- On the flattened data frame one can execute queries employing engines like Spark-SQL
	
## Sample JSON
```javascript
{
    "data": {
        "item": {
            "props": [
                {
                    "neat": "wow"
                },
                {
                    "neat": "tubular"
                }
            ]
        },
        "colors": [
            {
                "color":"red",
                "hex":  "ff0000"
            },
            {
                "color":"blue",
                "hex":"0000ff"
            }
        ]
    }
}
```
_The JSON is for illustration purpose. For Spark SQL you have to pass your json as a single line text._

-----------
HOW TO USE?
-----------
- Build the maven project.
- Run _RuleEngineDriver_ as Scala Application
--------------
PROGRAM OUTPUT
--------------
raw data frame
+--------------------+
|                data|
+--------------------+
|[[[red, ff0000], ...|
+--------------------+

flattened data frame
+-----------------+---------------+--------------------+
|data_colors_color|data_colors_hex|data_item_props_neat|
+-----------------+---------------+--------------------+
|              red|         ff0000|                 wow|
|              red|         ff0000|             tubular|
|             blue|         0000ff|                 wow|
|             blue|         0000ff|             tubular|
+-----------------+---------------+--------------------+

Applying Rule: Find colors with neat='wow'
Post Rule Data Frame
+-----------------+---------------+--------------------+
|data_colors_color|data_colors_hex|data_item_props_neat|
+-----------------+---------------+--------------------+
|              red|         ff0000|                 wow|
|             blue|         0000ff|                 wow|
+-----------------+---------------+--------------------+


	