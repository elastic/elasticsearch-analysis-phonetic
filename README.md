Phonetic Analysis for Elasticsearch
===================================

The Phonetic Analysis plugin integrates phonetic token filter analysis with elasticsearch.

## Version 2.4.3-SNAPSHOT for Elasticsearch: 1.4

If you are looking for another version documentation, please refer to the 
[compatibility matrix](http://github.com/elasticsearch/elasticsearch-analysis-phonetic#phonetic-analysis-for-elasticsearch).


## User guide

A `phonetic` token filter that can be configured with different `encoder` types: 
`metaphone`, `doublemetaphone`, `soundex`, `refinedsoundex`, 
`caverphone1`, `caverphone2`, `cologne`, `nysiis`,
`koelnerphonetik`, `haasephonetik`, `beidermorse`

The `replace` parameter (defaults to `true`) controls if the token processed 
should be replaced with the encoded one (set it to `true`), or added (set it to `false`).

```js
{
    "index" : {
        "analysis" : {
            "analyzer" : {
                "my_analyzer" : {
                    "tokenizer" : "standard",
                    "filter" : ["standard", "lowercase", "my_metaphone"]
                }
            },
            "filter" : {
                "my_metaphone" : {
                    "type" : "phonetic",
                    "encoder" : "metaphone",
                    "replace" : false
                }
            }
        }
    }
}
```

Note that `beidermorse` does not support `replace` parameter.


Questions
---------

If you have questions or comments please use the [mailing list](https://groups.google.com/group/elasticsearch) instead
of Github Issues tracker.

License
-------

    This software is licensed under the Apache 2 license, quoted below.

    Copyright 2009-2014 Elasticsearch <http://www.elasticsearch.org>

    Licensed under the Apache License, Version 2.0 (the "License"); you may not
    use this file except in compliance with the License. You may obtain a copy of
    the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
    License for the specific language governing permissions and limitations under
    the License.
