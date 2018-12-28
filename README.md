# chat
#Java#MySQL#SpringBoot#SpringMVC#Hibernate

Демо версия "веб-чата на один раз" - данный проект создан в целях ознакомиться с инструментами, применяемыми для разработки веб приложений на Java.

"Веб-чат на один раз" - приложение полезно для тех, кому необходимо вести переговоры разово с дальнейшим удалением всех данных из БД.

Чтобы запустить данный проект у себя на машине, необходимо иметь JDK8+, MySQL, Gradle.
Запуск приложения производится через класс ChatApplication.java, необходимо будет перейти на localhost:8080 и залогиниться.

Основные преимущества:

1)Когда пользователь залогинился, ему присваивается сессионные cookie(существуют, пока пользователь не закрыл браузер) и у него появляется время его активности на сайте.Если пользователь не совершает никаких действий в течение 5 минут, то его автоматически разлогинивает сервер и удаляет все его данные и его самого из БД.

2)Если пользователь залогинился и пока еще является активным пользователем(т.е. не прошло больше 5 минут с момента его последнего действия),то закрыв вкладку с чатом, при этом не закрывая целиком браузер, при повторном переходе на localhost:8080 его автоматически перебросит на главную страницу чата, где он может продолжить общаться.

3)Если пользователь с такими данными уже существует, то выдает alert "Already logged in", а также стоит ограничение на login и password - не меньше 1 символа и не больше 25 символов. 
