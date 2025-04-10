# shopper
Витрина интернет-магазина

## Запуск проекта

1. Убедитесь, что у вас установлены Java и Maven, а также Tomcat или Jetty.
2. Клонируйте репозиторий:
    ```bash
    git clone https://github.com/shigarov/shopper.git
    ```
3. Перейдите в директорию проекта:
    ```bash 
    cd <project-directory>
    ```
4. Соберите проект:
    ```bash 
    gradlew clean build
    ```
5. Запустите приложение:
    ```bash 
    java -jar build/libs/blogger-2.0-SNAPSHOT.jar
    ```

6. Наполните базу данных товарами:

    Используя CURL на Windows
    ```bash 
    populate.bat
    ```
    Используя CURL на Linux
    ```bash 
    populate.sh
    ```
   
6. Откройте браузер и перейдите по адресу:
    ```bash 
    http://localhost:8081
    ```
7. Остановите приложение:
   ```bash
   curl -X POST http://localhost:8081/actuator/shutdown
   ```

## Функциональность

### Наполнение базы данных товарами

```bash
curl -i -X POST -H "Content-Type: multipart/form-data" \
  -F "title=Велосипед BMC Speedmachine" \
  -F "description=Шоссейный велосипед BMC Speedmachine 01 ONE Red AXS (2024), S, белый" \
  -F "imageFile=@example/bmc.jpg" \
  -F "price=999999.00" \
  localhost:8081/admin/items/add
```

