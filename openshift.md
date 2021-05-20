## Deploying the app on Openshift

- Login into the cluster using `oc login`.

- Create a new project.

```
oc new-project sf-quarkus
```

- Clone the `orders-ms-quarkus` repo.

```bash
git clone https://github.com/ibm-garage-ref-storefront/orders-ms-quarkus.git
cd orders-ms-quarkus
```

- Setup the database.

```bash
cd database_setup
./setup_database.sh sf-quarkus
cd ..
```

- Include the OpenShift extension like this:

```
./mvnw quarkus:add-extension -Dextensions="openshift"
```

This will add the below dependency to your pom.xml

```
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-openshift</artifactId>
</dependency>
```

- Now, navigate to `src/main/resources/application.properties` and add the below.

```
quarkus.openshift.env.vars.orders-mariadb-host=orders-mariadb
quarkus.openshift.env.vars.orders-mariadb-port=3307
quarkus.openshift.env.vars.orders-mariadb-username=dbuser
quarkus.openshift.env.vars.orders-mariadb-password=password

quarkus.openshift.env.mapping.keycloak-client-id.from-secret=ibm-java-env
quarkus.openshift.env.mapping.keycloak-client-id.with-key=KEYCLOAK_CLIENT_ID
quarkus.openshift.env.mapping.keycloak-client-secret.from-secret=ibm-java-env
quarkus.openshift.env.mapping.keycloak-client-secret.with-key=KEYCLOAK_CLIENT_SECRET

quarkus.oidc.auth-server-url=https://keycloak-keycloak.mq-devops-6ccd7f378ae819553d37d5f2ee142bd6-0000.par01.containers.appdomain.cloud/auth/realms/sfrealm
```

- To trigger a build and deployment in a single step, run the below command.

```
./mvnw clean package -Dquarkus.kubernetes.deploy=true
```

If it is run successfully, you will see something like this.

```
[INFO] [io.quarkus.container.image.openshift.deployment.OpenshiftProcessor] Successfully pushed image-registry.openshift-image-registry.svc:5000/sf-quarkus-openshift/orders-ms-quarkus@sha256:f985cd157f5b83766dc57352b183003a373ff9de1d2f54baf8a33379ed432773
[INFO] [io.quarkus.kubernetes.deployment.KubernetesDeployer] Deploying to openshift server: https://c103-e.jp-tok.containers.cloud.ibm.com:31780/ in namespace: sf-quarkus-openshift.
[INFO] [io.quarkus.kubernetes.deployment.KubernetesDeployer] Applied: Service orders-ms-quarkus.
[INFO] [io.quarkus.kubernetes.deployment.KubernetesDeployer] Applied: ImageStream orders-ms-quarkus.
[INFO] [io.quarkus.kubernetes.deployment.KubernetesDeployer] Applied: ImageStream openjdk-11.
[INFO] [io.quarkus.kubernetes.deployment.KubernetesDeployer] Applied: BuildConfig orders-ms-quarkus.
[INFO] [io.quarkus.kubernetes.deployment.KubernetesDeployer] Applied: DeploymentConfig orders-ms-quarkus.
[INFO] [io.quarkus.deployment.QuarkusAugmentor] Quarkus augmentation completed in 190698ms
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  03:25 min
[INFO] Finished at: 2021-03-19T16:42:32+05:30
[INFO] ------------------------------------------------------------------------
```

- Now create the route as follows.

```
oc expose svc orders-ms-quarkus
```

- Grab the route.

```
oc get route orders-ms-quarkus --template='{{.spec.host}}'
```

You will see something like below.

```
$ oc get route orders-ms-quarkus --template='{{.spec.host}}'
orders-ms-quarkus-sf-quarkus-dev.mq-devops-6ccd7f378ae819553d37d5f2ee142bd6-0000.par01.containers.appdomain.cloud
```

- Now access the endpoint using `http://<route_url>/health`.

For instance if using the above route, it will be https://orders-ms-quarkus-sf-quarkus-dev.mq-devops-6ccd7f378ae819553d37d5f2ee142bd6-0000.par01.containers.appdomain.cloud/health.
