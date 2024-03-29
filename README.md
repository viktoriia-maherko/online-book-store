# ONLINE-BOOK-STORE

---
![My Image](book.png)
## Project Description
 I adore to read books and often buy them online. I think Online Book store is very useful, 
 so I decided to implement an app for Online Book store. This application supports authentication and registration of users.
 [Here](#things-shoppers-can-do) you can read about things shoppers can do. 
 [Here](#things-managers-can-do) you can read about things managers can do
---
## Navigation
1. [Technologies](#-technologies)
2. [Domain Models (Entities)](#domain-models-entities)
3. [Database structure](#database-structure)
4. [People involved](#people-involved)
5. [Application API](#application-api)
6. [How You Can Set Up And Use This Project](#-how-you-can-set-up-and-use-this-project)
---
## ⚙️ Technologies
- JDK 17;
- Git;
- Apache Maven;
- Spring Boot;
- Spring Security;
- Spring Data JPA;
- Swagger;
- My SQL;
- Junit 5;
- Liquibase;
- Docker;
- JWT tokens;
- Lombok;
- Mockito, JUnit tests
---
## 📂 Domain Models (Entities):
- **User:** Contains information about the registered user including their authentication details and personal information.
- **Role:** Represents the role of a user in the system, for example, admin or user.
- **Book:** Represents a book available in the store.
- **Category:** Represents a category that a book can belong to.
- **ShoppingCart:** Represents a user's shopping cart.
- **CartItem:** Represents an item in a user's shopping cart.
- **Order:** Represents an order placed by a user.
- **OrderItem:** Represents an item in a user's order.
---
## 📈 Database Structure
![Diagram](db_diagram.png)
## 🧑‍💻 People involved:
- **Shopper (User):** Someone who looks at books, puts them in a basket (shopping cart), and buys them.
- **Manager (Admin):** Someone who arranges the books on the shelf and watches what gets bought.
### Things Shoppers Can Do:
  1. **Join and sign in:**
-  Join the store.
-  Sign in to look at books and buy them.
  2. **Look at and search for books:**
-  Look at all the books.
-  Look closely at one book.
  Find a book by typing its name.
 3. **Look at bookshelf sections:**
-  See all bookshelf sections.
-  See all books in one section.
 4. **Use the basket:**
-  Put a book in the basket.
-  Look inside the basket.
-  Take a book out of the basket.
 5. **Buying books:**
-  Buy all the books in the basket.
-  Look at past receipts.
 6. **Look at receipts:**
-  See all books on one receipt.
-  Look closely at one book on a receipt.
### Things Managers Can Do:
 1.  **Arrange books:**
-   Add a new book to the store.
-   Change details of a book.
-   Remove a book from the store.
 2.  **Organize bookshelf sections:**
-   Make a new bookshelf section.
-   Change details of a section.
-   Remove a section.
 3.  **Look at and change receipts:**
-   Change the status of a receipt, like "Shipped" or "Delivered".
---
## Application API
[Book Controller](#book-controller) | [Authentication Controller](#authentication-controller) | [Category Controller](#category-controller) 
 
[Shopping Cart Controller](#shopping-cart-controller) | [Order Controller](#order-controller) 
### Book Controller
**Book Endpoints:**

- GET: /books (Retrieve book catalog)

Example of response body:
```json
[
{
 "id": 1,
 "title": "Harry Potter",
 "author": "Rouling",
 "isbn": "12345678",
 "price": 100,
 "description": "Story about magic world",
 "coverImage": "http://example.com/cover1.jpg"
 },
 {
 "id": 2,
 "title": "The Lord of The Rings",
 "author": "Tolkien",
 "isbn": "12345687",
 "price": 150,
 "description": "Story about rings of the power",
 "coverImage": "http://example.com/cover2.jpg"
}
]
```

- GET: /books/{id} (Retrieve book details)

Example of response body:
```json
{
  "id": 1, 
  "title": "Harry Potter",
  "author": "Rouling",
  "isbn": "12345678",
  "price": 100,
  "description": "Story about magic world",
  "coverImage": "http://example.com/cover1.jpg"
}
```

- POST: /books (Create a new book)

Example of request body:
```json
{
 "title": "Sample Book 3",
 "author": "Author C",
 "isbn": "9781122334455",
 "price": 29.99,
 "description": "Yet another sample book description.",
 "coverImage": "http://example.com/cover3.jpg"
}
```
- PUT: /books/{id} (Update a specific book)

Example of request body:
```json
{
 "title": "Updated Title",
 "author": "Updated Author",
 "isbn": "978-1234567890",
 "price": 19.99,
 "description": "Updated description",
 "coverImage": "https://example.com/updated-cover-image.jpg"
}
```

- DELETE /books/{id} (Delete a specific book)
- GET /books/search (Retrieve the book catalog based on search params.)
### Authentication Controller
**Authentication Endpoints:**
- POST: /auth/register (User registration)

Example of request body:
```json
{
 "email": "bob@gmail.com",
 "password": "qwerty12345",
 "repeatPassword": "qwerty12345",
 "firstName": "Bob",
 "lastName": "Smith",
 "shippingAddress": "USA"
}
```
Example of response body:
```json
{
 "id": 1,
 "email": "bob@gmail.com",
 "firstName": "Bob",
 "lastName": "Smith",
 "shippingAddress": "USA"
}
```
- POST: /auth/login (User authentication)

Example of request body:
```json
{
 "email": "john.doe@example.com",
 "password": "securePassword123"
}
```
Example of response body:
```json
{
 "token": "eyJhbGciOiJJ9.eyJzdWIiOiIxMjM0yfQ.SflKssw5c"
}
```
### Category Controller
**Category Endpoints:**
- POST: /categories (Create a new category)
Example of request body:
```json
{
 "name": "Fiction",
 "description": "Fiction books"
}
```
- GET: /categories (Retrieve all categories)

Example of response body:
```json
{
 "id": 1,
 "name": "Fiction",
 "description": "Fiction books"
}
```
- GET: /categories/{id} (Retrieve a specific category by its ID)
- PUT: /categories/{id} (Update a specific category)

Example of request body:
```json
{
 "name": "Fiction",
 "description": "Fiction books"
}
```
- DELETE: /categories/{id} (Delete a specific category)
- GET: /categories/{id}/books (Retrieve books by a specific category)
### Shopping Cart Controller
**Shopping Cart Endpoints:**
- GET: /cart (Retrieve user's shopping cart)

Example of response body:
```json
{
 "id": 123,
 "userId": 456,
 "cartItems": [
{
 "id": 1,
 "bookId": 789,
 "bookTitle": "Sample Book 1",
 "quantity": 2
},
{
 "id": 2,
 "bookId": 790,
 "bookTitle": "Sample Book 2",
 "quantity": 1
}
]
}
```
- POST: /cart (Add book to the shopping cart)

Example of request body:
```json
{
 "bookId": 2,
 "quantity": 5
}
```
- PUT: /cart/cart-items/{cartItemId} (Update quantity of a book in the shopping cart)

Example of request body:
```json
{
 "quantity": 10
}
```
- DELETE: /cart/cart-items/{cartItemId} (Remove a book from the shopping cart)
### Order Controller
**Order Endpoints:**
- POST: /orders (Place an order) 

Example of request body:
```json
{
  "shippingAddress": "Kyiv, Shevchenko ave, 1"
}
```
- GET: /orders (Retrieve user's order history)

Example of response body:
```json
[
{
 "id": 101,
 "userId": 456,
 "orderItems": [
{
 "id": 1,
 "bookId": 789,
 "quantity": 2
},
{
 "id": 2,
 "bookId": 790,
 "quantity": 1
}
],
 "orderDate": "2023-07-25T10:15:30",
 "total": 29.98,
 "status": "COMPLETED"
},
{
 "id": 102,
 "userId": 456,
 "orderItems": [
{
 "id": 3,
 "bookId": 791,
 "quantity": 1
}
],
 "orderDate": "2023-07-23T15:20:45",
 "total": 14.99,
 "status": "PENDING"
}
]
```
- PATCH: /orders/{id} (Update order status) 

Example of request body:
```json
{
"status": "DELIVERED"
}
```
**OrderItem Endpoints:** Since OrderItem entities are related to a specific order, you can create nested endpoints under an Order. This way, you can manage OrderItems within the context of their associated order.

- GET: /orders/{orderId}/items (Retrieve all OrderItems for a specific order)

Example of response body:
```json
[
{
 "id": 1,
 "bookId": 789,
 "quantity": 2
},
{
 "id": 2,
 "bookId": 790,
 "quantity": 1
}
]
```
- GET: /orders/{orderId}/items/{itemId} (Retrieve a specific OrderItem within an order)

Example of response body:
```json
{
 "id": 2,
 "bookId": 790,
 "quantity": 1
}
```
POST, PUT, and DELETE endpoints for OrderItem may not be necessary, as OrderItems are typically created, updated, or removed when a user interacts with their shopping cart or places an order.

---
## 🦶 How You Can Set Up And Use This Project
1. [x] Fork this repository;
2. [x] Clone forked repository;
3. [x] [Install](https://www.mysql.com/downloads/) MySQL;
4. [x] Create a database for your application and configure it in the application.properties file;
5. [x] [Install](https://www.docker.com/products/docker-desktop/) Docker;
6. [x] Create .env file. Example this file you can find in file .env.sample;
7. [x] Run the following command to build and start the Docker containers:

```text
docker-compose up --build
```
8. [x] Enjoy using this application. Good luck! 😊
