# Реализация сервера для загрузки видео
1.  Простейшая авторизация по логину, без пароля. 
* Реализовано по эндпоинту /api/register (через отправку JSON c ключем username).
2.  Каждый пользователь может параллельно загружать не более 2х видео (в разных вкладках)
3.  При попытке загрузить третье видео параллельно - ошибка
4.  При попытке загрузить не видео - ошибка. 
* Проверка осуществляется с помощью Apache Tika.
5.  Пользователю доступна страница со списком своих видео и возможностью скачать.
6.  Пользователю с логином admin доступен дашборд, где видно таблицу с текущими загрузками (логин, название видео, прогресс загрузки). 
* Эндпоинт подготовлен.
* Реализована идея открытия web-socket канала, по которому передаются данные в контроллер дашборда и на предполагаемый фронтенд из метода с загрузкой видео.
* Прогресс загрузки высичляется следующим образом: при загрузке файла он читается побайтно и в этом цикле считается процент завершенной загрузки (чтения) и информация о прогрессе должна передаваться по каналу /topic/progress.
* При тестировании обнаружено, что соединение устанавливается, но данные до контроллера с дашбордом не приходят, в результате по эндпоинту /admin/dashboard будет отображаться пустой список. Необходимо исправить баг.
8.  Одинаковые видео не занимают место на диске. 
* Не реализовано, планировалось использовать идею определения уникальности видео через вычисление и сравнение хэшей видео.

## OpenAI документация: 
http://localhost:8080/swagger-ui/index.html

## How to run:
java -jar /target/upload-web-server.jar

## Логика работы
### Запуск и регистрация

* При запуске приложения автоматически создается директория uploaded-videos рядом с jar файлом (или в корне проекта, если запуск из IDEA).
* Далее по эндпоинту необходимо зарегистрироваться, указав логин.
* После этого при работе с эндпоинтами в заголовок Authorization должен быть usernamе в рамках базовой аутентификации.
* Для получения доступа к дашборду админа нужно дополнительно зарегистрироваться с логином admin.
* После завершения работы директория uploaded-videos автоматически удаляется.

### Загрузка видео
* Загрузка видео-файла осуществляется на диск в директорию uploaded-videos.
* При записи на диск имя видео-файла кодируется в UTF-8.
* В application.properties указаны spring.servlet.multipart.max-file-size=2048MB, spring.servlet.multipart.max-request-size=2048MB