ssh azure "fuser -k 6666/tcp"
mvn package
scp target/GameLoader-1-shaded.jar tkacper@azure:serv.jar
ssh azure "java -cp serv.jar GameLoader/server/ServerRun"
