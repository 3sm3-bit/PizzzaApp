//
//  InfoStrings.swift
//  iosApp
//

import Foundation

struct InfoStrings {
    static let guideHowToUseApp = """
    <b>¡Bienvenido a Pizzzeria!</b> Disfruta de tus pizzas y platillos favoritos de forma rápida, fácil y segura siguiendo estos simples pasos:<br><br>
    <b>1. Crea tu cuenta (Registro)</b><br>
    • Antes de hacer tu primer pedido, es indispensable que tengas una cuenta creada. Si eres nuevo, toca en <b>"Regístrate ahora"</b> desde la pantalla principal e ingresa tus datos personales. Sin un usuario registrado no podrás iniciar sesión ni realizar pedidos.<br><br>
    <b>2. Inicia Sesión</b><br>
    • Una vez registrado, ingresa a la aplicación con tu usuario (o correo electrónico) y tu contraseña.<br>
    • <i>Dato útil:</i> Si en algún momento tienes dudas o necesitas soporte antes de iniciar sesión, puedes tocar el enlace <b>"Información y Ayuda"</b> ubicado en la parte inferior de la pantalla de acceso.<br><br>
    <b>3. Arma tu pedido (Menú principal)</b><br>
    • Al entrar verás el menú principal (Home) con diferentes secciones de pizzas, bebidas y agregados.<br>
    • Toca el producto que desees para personalizarlo a tu gusto: elige el tipo de masa, añade una deliciosa orilla rellena de queso o escribe notas especiales para la cocina.<br><br>
    <b>4. Revisa tu Carrito y elige la Entrega</b><br>
    • Todo lo que vayas seleccionando se guardará automáticamente en tu <b>Carrito</b>.<br>
    • Desde ahí podrás elegir cómo deseas recibir tu pedido: <b>Entrega a domicilio (Delivery)</b> o <b>Recojo en local</b>.<br>
    • Si eliges Delivery, podrás verificar, ajustar o cambiar la dirección exacta de entrega directamente en el mapa interactivo.<br><br>
    <b>5. Resumen y Pago Seguro</b><br>
    • Al continuar, verás el <b>Resumen del Pedido</b> con el detalle de los precios, el costo de envío y el total a pagar.<br>
    • Al presionar el botón de pago, la app te conectará de forma segura con la pasarela de pagos oficial (<b>Stripe</b>).<br>
    • 🔒 <b>Recomendación de seguridad:</b> Realiza tu transacción en un lugar privado, asegúrate de que ninguna persona extra esté viendo tu pantalla o tus datos bancarios al ingresar tu tarjeta, y nunca compartas tus contraseñas con nadie.<br><br>
    <b>6. Sigue el estado de tu pedido (Mis Órdenes)</b><br>
    • ¡Listo! Una vez que el pago sea exitoso, tu pedido quedará confirmado.<br>
    • Ve a la sección de <b>Órdenes</b> para monitorear el estado de tu pedido en tiempo real (<i>Pendiente, Confirmado, Preparándose, En camino</i>) hasta que el repartidor toque la puerta de tu hogar.
    """

    static let guideForgotData = """
    <b>¿Qué hacer si olvidaste tus datos de acceso?</b><br>
    Si creaste tu usuario y por alguna razón no recuerdas tus datos para iniciar sesión (lo cual es necesario para usar la app), y al intentar crear otro usuario el sistema te bloquea porque no se permiten datos repetidos, ten en cuenta lo siguiente:<br><br>
    <b>1. Restricción por datos repetidos</b><br>
    • Por políticas de la empresa, no está permitido crear más de un usuario con datos repetitivos o idénticos (como correo, teléfono o documentos ya registrados). Si intentas registrarte nuevamente con la misma información, el sistema no te dejará avanzar.<br><br>
    <b>2. Eliminación de usuario en sucursal</b><br>
    • Para poder utilizar tus datos habituales nuevamente, debes proceder con la eliminación de tu usuario anterior.<br>
    • Este proceso se realiza acudiendo a cualquiera de los establecimientos registrados como sucursal. Puedes verificar las direcciones de las sucursales autorizadas en nuestra página web: <a href="https://lapizzzeria.com/conoce-mas-sobre-la-pizzzeria/"><b>Ver sucursales de Pizzzeria</b></a>.<br>
    • Al acudir a la sucursal, debes recurrir al personal autorizado, explicar tu caso y el asesor de atención procederá a la eliminación de tu usuario para que puedas crearte uno nuevo.<br><br>
    <b>3. Crear usuario con otros datos</b><br>
    • En caso de que no desees realizar todo este proceso en sucursal, la otra opción es crear otro usuario utilizando <b>datos diferentes</b>, ya que por políticas internas no se permite tener más de un registro con datos repetidos.
    """

    static let guideTermsAndConditions = """
    <b>TÉRMINOS Y CONDICIONES DE USO Y SERVICIO</b><br><br>
    <b>1. Objeto y Alcance</b><br>
    Bienvenido a <b>Pizzzeria</b>. Los presentes Términos y Condiciones regulan el uso de nuestra aplicación móvil y servicios de comercio electrónico para la venta y entrega de alimentos preparados (pizzas, bebidas y acompañamientos). Al utilizar la app, aceptas estas disposiciones.<br><br>
    <b>2. Procesamiento de Pagos y Pasarelas (Stripe)</b><br>
    • Las transacciones de pago dentro de nuestra plataforma se procesan a través de la pasarela de pagos segura <b>Stripe</b> (y/o plataformas externas autorizadas). Es dicha empresa quien procesa y efectúa los cobros de manera independiente, liberando de toda responsabilidad directa sobre la plataforma de cobro a Pizzzeria.<br>
    • La información financiera y sensible se procesa de forma cifrada en servidores seguros de la pasarela de pago. Pizzzeria almacena únicamente datos de transacciones para efectos de control interno, facturación y contabilidad.<br><br>
    <b>3. Información Nutricional e Ingredientes de los Alimentos</b><br>
    • Tratándose de productos alimenticios artesanales, los alimentos comercializados son consistentes con la publicidad y fotografías ofrecidas en la aplicación.<br>
    • Se incluye el detalle de especificaciones (dimensiones, porciones), ingredientes principales y advertencias sobre alérgenos (como gluten, lácteos y otros componentes de sodio o grasas).<br><br>
    <b>4. Condiciones de Pago, Facturación y Comprobantes</b><br>
    • Se detallan las condiciones de pago mediante tarjeta y los medios oficiales para obtener el comprobante fiscal o de transacción comercial, así como el procedimiento aplicable para solicitar correcciones cuando corresponda.<br><br>
    <b>5. Garantías, Tiempos y Formas de Entrega</b><br>
    • Se establecen los tiempos estimados de entrega (garantías de tiempo) aplicables a nuestros alimentos a domicilio o recojo en local, así como las formas y lugares de entrega autorizados.<br><br>
    <b>6. Seguridad e Inviolabilidad de la Información</b><br>
    • Nuestra plataforma cuenta con certificados digitales y protocolos de seguridad robustos para garantizar la protección e inviolabilidad de la información de los usuarios.<br><br>
    <b>7. Contacto y Reclamaciones</b><br>
    Para cualquier reclamación, consulta o aclaración relacionada con el servicio, puedes comunicarte a través de nuestros canales oficiales o soporte en línea de la app.
    """
}
