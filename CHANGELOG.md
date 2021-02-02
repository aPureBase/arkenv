
# 3.1.0
* Added module support.

# 3.0.0
* #5 The profile feature is now installed by default
* #17 Enable access to the active profiles

# 2.2.0
* #6 Add default mappings for primitive arrays, List<String>, and Collection<String>
* #1 Fix encryption api only works on java < 9

# 2.1.0
> Published 2019-09-02

[All issues](https://gitlab.com/apurebase/arkenv/issues?scope=all&utf8=%E2%9C%93&state=all&milestone_title=v2.1.0)

* Introduced ProcessorFeatures that allow to define custom processing logic that is executed on all loaded data
* Added Encryption processing feature
* Added Yaml support for properties / profiles
* Added basic Http feature
* The ProfileFeature now supports multiple profiles
* Added get and set functions to Arkenv to allow retrieving unparsed arguments
* EnvironmentVariableFeature can be configured via arguments
* Fix #77 .env file parsing interprets all `=` as new declaration
