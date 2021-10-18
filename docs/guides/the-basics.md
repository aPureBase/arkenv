---
layout: default
title: The Basics
parent: Guides
nav_order: 1
---

# The Basics

Arkenv provides a sensible set of defaults that aim to support a variety
of configuration scenarios whilst making it easy to adapt to changing 
environments.

Below is a sample Arkenv configuration that defines arguments for a 
MySQL password and a port. 

```kotlin
object Configuration {
    val mysqlPassword: String by argument()
    val port: Int by argument()
}
```

Your app might look something like this:
```kotlin
fun main(args: Array<String>) {
    Arkenv.parse(Configuration, args)
    Database(Configuration.mysqlPassword).connect()
    Server(Configuration.port).start()
}
```

### Command line
To begin with, you could start by passing arguments from the command line
 or via your IDE to verify everything works. 
 
```bash
java -jar App.jar --mysql-password c0mpl3x --port 5000
```

### Environment Variables
You can also use environment variables to achieve the same.
```bash
export MYSQL_PASSWORD=ch4ng3d
export PORT=5001
```

### Profiles
At some point you may want to have varying configuration without the need
to change your command line arguments every time. 

You may want to create profiles based on different environments that the
app is going to run in, e.g. dev, test, production, etc. 

Start by adding `application.properties` to your resources folder.
```properties
MYSQL_PASSWORD=c0mpl3x
PORT=5000
```
This is the base profile which will always be loaded, but can be overridden.
 Now you can run the program without passing any arguments from the command line.

Next, create a file for each profile called `application-${profile}.properties`
in your resources folder. 

For example `application-prod.properties`:
```properties
MYSQL_PASSWORD=d1ff3r3nt
PORT=443
``` 

In order to activate one or more of these profiles, you can pass a 
comma-separated list via the command line or an environment variable.

If you wanted to activate the profiles `prod` and `secret`: 

Command line:
```bash
java -jar App.jar --arkenv_profile prod,secret
```

Environment variable:
```bash
export ARKENV_PROFILE=prod,secret
```
___
ℹ️ You can also use yaml for your profiles. 
See [YamlFeature]({{site.baseurl}}features/yaml). 

### Docker
For local development you may be using docker-compose to provide 
the database or other services.

You can easily integrate the `.env` file that is used 
for compose with Arkenv.

Use either cli, env vars, or a profile to specify the name of 
the dot env file that you want to use as a source.
```
ARKENV_DOT_ENV_FILE=.env
```


### Conclusion
By default, Arkenv provides you with a flexible set of configuration sources
that enable you to externalize your configuration without having to think about
how it is loaded. 

Head over to the [feature section]({{site.baseurl}}features/features) to find out
what else is possible. 

