kubectl create namespace baat --dry-run=client -o yaml | kubectl apply -f -
kubectl delete -f infrastructure/k8s/service-deployment.yml --namespace=baat
kubectl create -f infrastructure/k8s/service-deployment.yml --namespace=baat
