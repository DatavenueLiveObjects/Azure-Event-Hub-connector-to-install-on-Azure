# Mqtt2EvtHub

Spring Boot project reading events from Live Objects MQTT and pushing them to Azure Event Hub

## Introduction

This project is intended for Live Objects users wishing to explore integration patterns with Azure and for organizations already running business logic on Azure planning to work on events from IoT devices sourced via Live Objects.

### Requirements

It is assumed that the reader of this document is familiar with Live Objects and Azure. In order to run the connector, it is required to have access to:
- a Live Objects account
  - with MQTT fifo queue
  - API key which can access the queue (API key generation is described in the [user guide](https://liveobjects.orange-business.com/#/cms/ressources-guide-utilisateur))
- an Azure account
  - Event Hub set up (creation process is described in official [documentation]( https://docs.microsoft.com/en-in/azure/event-hubs/))
  - Primary Key to event hub (it is automatically generated by Azure, see `all resources -> your event hubs namespace -> Shared access policies`)
- computer with
  - Java-supporting IDE (e.g. IntelliJ, Eclipse)
  - Azure CLI (https://docs.microsoft.com/en-us/cli/azure/?view=azure-cli-latest)
  - Apache Maven 
  - Git client

## Connector

The connector (Mqtt2EvtHub) subscribes to selected Live Objects MQTT queue, reads all events and publishes them to selected Azure Event Hub without any modification to events’ contents. It is intended to be run as a long-running process hosted on Azure. Connector code is written in Java, using the Spring Boot framework. 

Mqtt2EvtHub supports only the communication from Live Objects i.e. it reads messages sent from IoT devices. Communication towards devices (sending commands to devices) is not supported.

Provisioning of IoT devices is within the scope of Live Objects; Mqtt2EvtHub has no knowledge on what devices are communicating; it is just transparently moving messages from Live Objects to Azure Event Hub. It is assumed that business logic acting on those messages is to be applied by applications consuming the messages from Event Hub. 

![architecture](/images/arch.png)

### Performance & scalability

The software is an open source toolbox which has to be integrated into an end to end solution. Mqtt2EvtHub comes without any guarantees related to percentage of messages successfully written to Event Hub, nor to the response time. Moreover, the ordering of messages is not guaranteed to be preserved; the application uses thread pools to run its MQTT and Event Hub adapters which may cause some messages to arrive in Event Hub out of order in which they were kept within Live Objects’ MQTT queue. 

Tested on Azure [P2v2]( https://azure.microsoft.com/en-us/pricing/details/app-service/linux/) App Service Plan, the connector was processing events at the rate of ~120,000 messages per minute (please keep in mind that Event Hub has a configurable throughput limit, with single [throughput unit]( https://azure.microsoft.com/en-us/pricing/details/event-hubs/) corresponding to ~60,000 messages per minute). This should not be treated as a guarantee either, since the throughput depends i.a. on message size and number of applications shared within App Service Plan.

Live Objects platform supports load balancing between multiple MQTT subscribers. It is possible to run multiple instances of Mqtt2EvtHub, each of them will handle its own subset of messages.

### Installation

In order to be deployed to Azure, the project uses Azure Webapp Maven Plugin. The installation description is based on the following tutorial: https://docs.microsoft.com/en-us/java/azure/spring-framework/deploy-spring-boot-java-app-with-maven-plugin
You can refer to it in case of troubles with current project, or if you want to better understand how it works.

#### App Service Plan creation

Application requires an [Application Service Plan]( https://docs.microsoft.com/en-us/azure/app-service/overview-hosting-plans) to run on Azure. App Service Plan defines compute resources available for the application during execution, exposing a runtime platform (Platform as a Service). It is a paid service, the price depending on CPU and RAM configuration.

To create a Plan, enter [Azure Web Portal]( https://portal.azure.com/) and select “create a resource” option. In the search field type “App Service Plan”, click “Create”. Now you need to provide a name, operating system (connector was tested on Linux, but it should work without problems on Windows as well), location, resource group and the size. Afterwards, click “Review and create”, then final “Create”.

### Configuration

Application is using standard Spring mechanisms for configuration, with all properties stored within `application.yml` file. Properties values can be kept there and/or overridden from other sources (e.g. from environment variables) – see more information in  [Spring documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html). One of possible methods for setting Spring app configuration parameters from Azure is to define `JAVA_OPTS` environment variable and place configuration values in it. For example, in order to specify `lo-mqtt.api-key` and `lo-mqtt.topic`, one would need to set `JAVA_OPTS` to:
```
-Dlo.api-key=apikey –Dlo.topic=fifo/topic
```
More information on `JAVA_OPTS` in Azure context can be found [here]( https://blogs.msdn.microsoft.com/azureossds/2015/10/09/setting-environment-variable-and-accessing-it-in-java-program-on-azure-webapp/).

The following properties are used for integration:

##### lo.hostname
URI of the Live Objects platform.
Default value `liveobjects.orange-business.com`
##### lo.api-key
API key to authorize the MQTT connector. Information on how to create a Live Objects API key can be found in [Live Objects documentation](https://liveobjects.orange-business.com/doc/html/lo_manual.html#API_KEY). 
There is no sensible default value since it should correspond to your account on Live Objects.
Connector uses [Application mode]( https://liveobjects.orange-business.com/doc/html/lo_manual.html#MQTT_MODE_APPLICATION) to connect to Live Objects Platform so `username` is internally set to `application` and it cannot be changed.
 
##### lo.topic
Name of the MQTT queue. For example, if Live Objects web portal displays a FIFO named “dev” (seen under “Data -> FIFO” option), to subscribe to this queue the property should be set to `dev`.
##### azure.evt-hub.name-space
Namespace in which your Event Hub is created.
##### azure.evt-hub.evt-hub-name
Name of Event Hub to which the MQTT messages should be written to.
##### azure.evt-hub.sas-key-name
It is a policy name. The default one, created by Azure, is named RootManageSharedAccessKey. You can verify the name and key value in the Azure portal, by checking `all resources -> your event hubs namespace -> Shared access policies`.
##### azure.evt-hub.sas-key
This is a `Primary key` from the policy above. 

#### App deployment

### Through Maven plugin

Now, get this github project on your development machine, using for instance: 
```
git clone https://github.com/DatavenueLiveObjects/Azure-Event-Hub-connector-to-install-on-Azure.git
```

Deployment to Azure is performed by the Azure Webapp Maven Plugin. Its configuration is included in `pom.xml` file within the connector project.

The following lines are relevant:
```
<resourceGroup>JavaApps</resourceGroup>
<appServicePlanName>PremiumV2Plan</appServicePlanName>
<appName>lo-event-hub</appName>
```

The `resourceGroup` and `appServicePlanName` should correspond to values provided during App Service Plan creation. `appName` will be used to uniquely identify the deployed connector app. Please keep in mind that `appName` must be unique application name in whole `azurewebsites.net` subdomain to avoid dns collision.

### Manual deployment to a Linux VM

It is also possible to use one of the releases at https://github.com/DatavenueLiveObjects/Azure-Event-Hub-connector-to-install-on-Azure/releases by manually executing the application on a Linux VM.

**Azure login**

In order to deploy the application, the prerequisite is to have a logged session to Azure. Run the following command using Azure CLI tool:

```
az login
```

Follow the instructions to complete the login process. 

**JAR deployment**

Build the JAR file using command:
```
mvn clean package
```
Deploy the application with the command:
```
mvn azure-webapp:deploy
```

If you repeat those steps, application will be redeployed, replacing the previously-deployed instance on Azure. Please keep in mind that you don’t have to repeat the login to Azure (unless the session expired, which will result in appropriate error message during app deployment).

### Acceptance check-up

To check if connector is deployed properly you can:
- go under `your_webapp_url/actuator/` to see some useful information, especially under `your_webapp_url/actuator/health`
- go to the web app log stream in Azure Portal - <YourAppName>, menu “Monitoring”, sub-menu “Log Stream”, to see the stdout of the application
- go to EventHub overview to see few charts with (among others) incoming and outcoming messages    

### Troubleshooting

If wrong configuration to EventHub is given, Azure connector will throw a `com.microsoft.azure.eventhubs.EventHubException` wrapping a `java.lang.NullPointerException`. In such case, make sure that you provide proper EventHub namespace, hub name, SAS key name and value.
