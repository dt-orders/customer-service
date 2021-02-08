# Overview

This repo has the code for the customer service for demostrations.  See the [overview](https://github.com/dt-orders/overview) repo for an overiew for that whole application.

The intent of this demo is to have these 3 versions of the application.

| Service  | Branch/Docker Tag | Description |
|---|:---:|---|
| customer-service | 1 | Normal behavior |
| customer-service | 2 | High Response time for /customer/list.html |
| customer-service | 3 | Normal behavior |

# LaunchDarkly

This application has also been build with the [LaunchDarkly Java SDK](https://docs.launchdarkly.com/sdk/server-side/java).  A flag called `enhanced-customer-list` has beed configured so that the value `ON` will enable the same High Response time as enabled in version 2. 

If you don't need LaunchDarkly, then there is no impact - it will only be enabled if you setup the LaunchDarkly KEY as described below in the `Run Docker Image` section.  

Be sure to setup the LaunchDarkly Dynatrace integration](https://docs.launchdarkly.com/integrations/dynatrace) as well to get LaunchDarkly changes pushed to Dynatrace.

# Run Docker Image

The images for this service has been build and published to Dockerhub.

1. Here is an example of running version 2
  ```
  docker run -p 8080:8080 dtdemos/dt-orders-customer-service:2
  ```

1. Here is an example of running version 1 with LaunchDarkly enabled

```
export LAUNCH_DARKLY_SDK_KEY=<YOUR KEY>
docker run -p 8080:8080 dtdemos/dt-orders-customer-service:2
```

2. access application at ```http://localhost:8080```

# URL reference

| url | type | description |
|-----|------|-------------|
| #.html | GET | get a customer by ID e.g. /1.html |
| form.html | GET | open form to edit a customer |
| form.html | POST | submit the form of an edited customer |
| health | GET | return JSON payload for application health - this response is hardcoded as OK |
| list.html | GET | list of customers |
| manifest | GET | show the DOCKER image manifest file |
| setversion/# | GET | set the version e.g. /setversion/1  /setversion/2 |
| showflags | GET | show settings for feature flags |
| version | GET | show the version |

# Developer Notes

## Pre-requisites

The following programs to be installed
* Java 14
* Maven
* Docker

## Build and Run Locally

1. Use the provided Unix shell scipt that will build one docker image and run it

    Just call: `./buildrun.sh <REPOSITORY> <VERSION>`

    For example, build and run version 1: `./buildrun.sh dtdemos 1`

2. access application at ```http://localhost:8080```

## Build Docker Images and push images to a repository

Use the provided Unix shell scipt that will build the docker image and publish it. There are three versions that will be built.

    Just call: `./buildpush.sh <REPOSITORY>`

    For example: `./buildpush.sh dtdemos`
