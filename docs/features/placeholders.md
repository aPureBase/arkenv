---
layout: default
title: Placeholders
parent: Features
nav_order: 10
---

# Placeholders

You can refer back to an already defined argument. 

### Usage
Declare a placeholder in this way: `${reference}`

Where reference is the name of:
* an existing argument in your Arkenv instance
* a argument passed from the command line
* any other key in `Arkenv::keyValue`, loaded via a feature
* a system environment variable

Here is an example of using a placeholder in a properties file:
```properties
app_name        = MyApp
app_description = ${app_name} is a Arkenv application
```


