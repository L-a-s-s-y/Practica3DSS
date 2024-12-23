# Practica 3 DSS

# Instalación y ejecución
Para generar el archivo `apk` en Android Studio, ir al menú de opciones y elegir
- Build -> Build App Bundle(s) / APK(s) -> Build APK(s)

El archivo apk se encontrará en el directorio del proyecto en la ruta: `app/build/outputs/apk/debug`

Para instalarla en el dispositivo, simplemente hay que descargar el archivo en el dispositivo android y ejecutar el apk (aceptar riesgos de seguridad y otras preguntas).
# Dependencias
- `Retrofit`
- `Okhttp`
- `GSON`
- `Osmdroid`
- `Androidx`
# Estructura y organización del código
Se ha estructurado el proyecto de la siguiente forma:
```
├───java
│   └───com
│       └───example
│           └───practica3
│               ├───adapters
│               ├───api
│               ├───login
│               ├───managers
│               ├───models
│               └───ui
│                   └───theme
└───res
    ├───drawable
    ├───layout
    ├───mipmap-anydpi-v26
    ├───mipmap-hdpi
    ├───mipmap-mdpi
    ├───mipmap-xhdpi
    ├───mipmap-xxhdpi
    ├───mipmap-xxxhdpi
    ├───values
    └───xml
```
Los directorios de interés son
- En el directorio principal se encuentran las `activities`:
	- MainActivity
	- CartActivity
	- MapActivity
	- LoginActivity
- El directorio `adapters` contiene el código de los adapter utilizados:
	- CartAdapter
	- CheckoutAdapter
	- ProductAdapter
- El directorio `api` contiene el código para hacer llamadas a la API del backend:
	- ApiClient
	- ApiService
- En el directorio `login` se encuentra el código adicional para poder llevar a cabo el login:
	- LoginRequest
	- LoginResponse
- En el directorio `managers` se localiza el código de los *managers* necesarios. Por ahora solo el del carrito:
	- CartManager
- En el directorio `models` se encuentran los modelos utilizados:
	- ResponseModel
	- ProductModel
- En el directorio `layouts` se encuentran los diseños de las vistas necesarias:
	- activity_cart
# API
- **GET** `/api/products`: endpoint que permite obtener la lista de productos disponibles.
- **POST** `/api/cart/add`: endpoint que permite añadir un producto de la lista de productos al carrito.
-  **GET** `/api/cart`: endpoint para obtener los productos que existen actualmente en el carrito.
-  **POST** `/api/cart/delete`: endpoint para eliminar un producto del carrito.
-  **POST** `/api/cart/payment`: endpoint para realizar el pago de los productos elegidos.
-  **POST** `/api/auth/login`: endpoint para realizar la autenticación.