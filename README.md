# Deploy as Knative serving

## Build Docker image

### Build with jib
```
mvn compile jib:build
```

### or Build to Docker daemon
```
mvn compile jib:dockerBuild
```

## Deploy
Push the image to Docker Hub, then deploy it:
```
kubectl apply -f event-display.yml
```
