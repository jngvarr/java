# Фреймворк Spring (семинары)
## Урок 5. Spring Data для работы с базами данных
### Задание:
Ваша команда разрабатывает приложение для управления проектами. Вам нужно реализовать функциональность связывания пользователей с проектами.

Создайте сущность "пользователь" (User), которая будет содержать следующие поля:
- Идентификатор (id) типа Long, генерируемый автоматически при создании записи
- Имя пользователя (username) типа String
- Пароль (password) типа String
- Электронная почта (email) типа String
- Роль (role) типа String

Создайте сущность "проект" (Project), которая будет содержать следующие поля:
- Идентификатор (id) типа Long, генерируемый автоматически при создании записи
- Название проекта (name) типа String
- Описание проекта (description) типа String
- Дата создания (createdDate) типа LocalDate

 Создайте абстрактный класс "сущность с связью" (EntityWithRelation), который будет содержать следующие поля:
- Идентификатор (id) типа Long, генерируемый автоматически при создании записи
- Идентификатор связанной сущности (relatedEntityId) типа Long

- Создайте сущность "пользователи проекта" (UsersProject), которая наследуется от класса "сущность с связью" и будет содержать следующие поля:
- Идентификатор проекта (projectId) типа Long
- Идентификатор пользователя (userId) типа Long

Создайте интерфейс репозитория (UserRepository), который будет расширять JpaRepository<User, Long>.
Создайте интерфейс репозитория (ProjectRepository), который будет расширять JpaRepository<Project, Long>.
Создайте интерфейс репозитория (UsersProjectRepository), который будет расширять JpaRepository<UsersProject, Long>.

Создайте сервисный класс (UserProjectService), который будет содержать следующие методы:
- public List getUsersByProjectId(Long projectId) - метод, возвращающий список пользователей, связанных с определенным проектом
- public List getProjectsByUserId(Long userId) - метод, возвращающий список проектов, связанных с определенным пользователем
- public void addUserToProject(Long userId, Long projectId) - метод, добавляющий пользователя к проекту
- public void removeUserFromProject(Long userId, Long projectId) - метод, удаляющий пользователя из проекта

Создайте контроллер (UserProjectController), который будет содержать следующие методы:
- public ResponseEntity<List> getUsersByProjectId(Long projectId) - метод, обрабатывающий GET-запрос для получения списка пользователей, связанных с определенным проектом
- public ResponseEntity<List> getProjectsByUserId(Long userId) - метод, обрабатывающий GET-запрос для получения списка проектов, связанных с определенным пользователем
- public ResponseEntity addUserToProject(Long userId, Long projectId) - метод, обрабатывающий POST-запрос для добавления пользователя к проекту
- public ResponseEntity removeUserFromProject(Long userId, Long projectId) - метод, обрабатывающий POST-запрос для удаления пользователя из проекта
