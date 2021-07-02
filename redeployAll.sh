kubectl create namespace baat
kubectl delete -f infrastructure/k8s/service-deployment.yml --namespace=baat
kubectl create -f infrastructure/k8s/service-deployment.yml --namespace=baat
