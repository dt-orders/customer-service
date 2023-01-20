# Overview

This repo has the code for the customer service for demonstrations.  See the [overview](https://github.com/dt-orders/overview) repo for an overview for that whole application.

The intent of this demo is to have versions of the application with normal and builtin problems that are controlled by the version number.

| Service  | Version/Docker Tag | Description |
|---|:---:|---|
| customer-service | 1 | Normal behavior |
| customer-service | 2 | High Response time for /customer/list.html |
| customer-service | 3 | High Response time for all requests |

# Run Docker Image

The images for this service has been build and published to Dockerhub.

1. Here is an example of running version 2

  ```
  docker run -p 8080:8080 dtdemos/dt-orders-customer-service:2
  ```

1. Here is an example of running version 1 with LaunchDarkly enabled

  ```
  export LAUNCH_DARKLY_SDK_KEY=<YOUR KEY>
  docker run -p 8080:8080 --env LAUNCH_DARKLY_SDK_KEY=$LAUNCH_DARKLY_SDK_KEY dtdemos/dt-orders-customer-service:1
  ```

1. Here is an example of running version 1 with Rookout enabled

  ```
  export ROOKOUT_TOKEN=<YOUR TOKEN>
  docker run -p 8080:8080 --env ROOKOUT_TOKEN=$ROOKOUT_TOKEN --env ROOKOUT_LIVE_LOGGER=True dtdemos/dt-orders-customer-service:1
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
| version | GET | show the version |

# Developer Notes

## Pre-requisites

The following programs to be installed
* Java 15
* Maven
* Docker

## Build and Run Locally

Use the provided Unix shell script that will build one docker image and run it.  Optionally pass in the dockerhub repo name and version to run.

* If using LaunchDarkly or Rookout, copy the `creds.template` file to `creds.json` and adjust the values.
* To build, create image and run docker locally, call: `./buildrun.sh`
* Access application at `http://localhost:8080/list.html`

## Build Docker Images and push images to a repository

Use the provided Unix shell script that will build the docker image and publish it. There are three versions that will be built.  Optionally pass in the dockerhub repo name.

* To build, create image, and push to dockerhub, call: `./buildpush.sh`

# LaunchDarkly Feature Flags

This application is built with the [LaunchDarkly Java SDK](https://docs.launchdarkly.com/sdk/server-side/java).   This is to demo "in-place" changes to application.

If you don't use LaunchDarkly, then there is no impact - it will only be enabled if you setup the LaunchDarkly SDK KEY set as a Docker environment variable. See `Run Docker Image` section above.  

Be sure to setup the LaunchDarkly Dynatrace integration](https://docs.launchdarkly.com/integrations/dynatrace) as well to get LaunchDarkly changes pushed to Dynatrace.

# Rookout Live Debugging

This application is built with the [Rookout Java SDK](https://docs.rookout.com/docs/jvm-setup).

If you don't use Rookout, then there is no impact - it will only be enabled if you setup the Rookout Token as a Docker environment variable. See `Run Docker Image` section above.  

The Rookout JAR file is in `app/` folder and was downloaded using the command on the [Rookout Java SDK](https://docs.rookout.com/docs/jvm-setup) webpage.