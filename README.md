## Vertx 3.x Kubernetes HA testing playground

Local Setup (m* mac)

    curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-darwin-arm64\n
    sudo install minikube-darwin-arm64 /usr/local/bin/minikube\n
    brew install qemu\n
    brew install socket_vmnet\n
    brew tap homebrew/services\n
    HOMEBREW=$(which brew) && sudo ${HOMEBREW} services start socket_vmnet\n
    minikube start --driver qemu --network socket_vmnet\n
    minikube addons enable ingress\n


To build:

    mvn package

run locally if required:

    java -jar target/service-1.0-SNAPSHOT-jar-with-dependencies.jar

make a new docker image:

    eval $(minikube docker-env)
    docker build . -t myapp-2 -f dockerfile

then apply changes (name of deployment above in service.yaml)

    kubectl delete -f service.yaml
    kubectl apply -f service.yaml
    kubectl rollout restart deployment myapp
    kubectl scale --replicas=3 deployment/myapp

expose the port
    kubectl get ingress
    sudo vim /etc/hosts
    add in the ip address from the ingress and the host host.com


check pods:

    kubectl get pods 
    kubectl get pods --namespace=ingress-nginx


    kubectl describe pod 

log into the pod to debug network issues if required:

    kubectl exec -it myapp-84f5447f85-mmhxp -- /bin/sh
    apt-get update && apt-get install -y net-tools lsof


test results:

    1. without any ingress in kube; nginx gives a 404
    2. with only an ingress applied, but the service removed; nginx gives 503
    3. with the pods starting up (Ready 0/1; before the readiness probe finishes) nginx give 503
    4. with pods ready 1/1; nginx responds with the proxied result with 200
    5. repeated kubectl rollout restart deployment myapp results in only 200
    6. kubectl scale --replicas=1 deployment/myapp *does* result in 502 returned if timed during active connections

    example logs:

        > kubectl logs ingress-nginx-controller-bc57996ff-6wkk7 --namespace=ingress-nginx

        2024/11/28 03:02:09 [error] 306#306: *31350 connect() failed (111: Connection refused) while connecting to upstream, client: 192.168.105.1, server: host.com, request: "GET / HTTP/1.1", upstream: "http://10.244.0.23:8888/", host: "host.com"
        2024/11/28 03:02:09 [error] 306#306: *31350 connect() failed (111: Connection refused) while connecting to upstream, client: 192.168.105.1, server: host.com, request: "GET / HTTP/1.1", upstream: "http://10.244.0.24:8888/", host: "host.com"
        2024/11/28 03:02:09 [error] 306#306: *31350 connect() failed (111: Connection refused) while connecting to upstream, client: 192.168.105.1, server: host.com, request: "GET / HTTP/1.1", upstream: "http://10.244.0.22:8888/", host: "host.com"
        192.168.105.1 - - [28/Nov/2024:03:02:09 +0000] "GET / HTTP/1.1" 502 150 "-" "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/119.0" 270 0.000 [default-myapp-8888] [] 10.244.0.23:8888, 10.244.0.24:8888, 10.244.0.22:8888 0, 0, 0 0.000, 0.000, 0.000 502, 502, 502 231285d9b34e994d85b195dc8ff832ba
