## Vertx 3.x Kubernetes HA testing playground

To build:

    mvn package

run locally if required:

    java -jar target/service-1.0-SNAPSHOT-jar-with-dependencies.jar

make a new docker image:

    docker build . -t myapp-2 -f dockerfile

then apply changes (name of deployment above in service.yaml)

    kubectl delete -f service.yaml
    kubectl apply -f service.yaml
    kubectl rollout restart deployment myapp
    
check pods:

    kubectl get pods 

    kubectl describe pod 

log into the pod

    kubectl exec -it myapp-84f5447f85-mmhxp -- /bin/sh
    apt-get update && apt-get install -y net-tools lsof

start a port forwarder:

    kubectl port-forward service/myapp 8888
