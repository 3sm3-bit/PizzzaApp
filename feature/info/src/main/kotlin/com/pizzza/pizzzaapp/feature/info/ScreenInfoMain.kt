package com.pizzza.pizzzaapp.feature.info

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pizzza.pizzzaapp.core.navigation.ScreenInitNav
import com.pizzza.pizzzaapp.core.ui.R
import com.pizzza.pizzzaapp.core.ui.utils.openEmail
import com.pizzza.pizzzaapp.core.ui.utils.openWhatsApp
import com.valu.uitaycompose.utils.COUNTRY_CODE_MX
import com.valu.uitaycompose.utils.extension.uiTayDialedNumber
import com.valu.uitaycompose.utils.extension.uiTayViewCall
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textB14
import com.valu.uitaycompose.utils.textB18
import com.valu.uitaycompose.utils.textB25
import com.valu.uitaycompose.utils.textM14
import com.valu.uitaycompose.utils.textS25

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenInfoMain(
    onNavigateTo: (ScreenInitNav) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 32.dp, horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "Canales de Contacto",
                style = textB25,
                color = tay_red_600,
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ContactCard(
                    title = "WhatsApp",
                    iconRes = R.drawable.ic_social_w,
                    onClick = {
                        context.openWhatsApp(
                            phone = "4492057452",
                            text = "¡Hola me gustaria consultar:",
                            code = COUNTRY_CODE_MX
                        )
                    }
                )
                Spacer(modifier = Modifier.width(30.dp))
                ContactCard(
                    title = "Correo",
                    iconRes = R.drawable.ic_email_contact,
                    onClick = {
                        context.openEmail()
                    }
                )

                Spacer(modifier = Modifier.width(30.dp))
                ContactCard(
                    title = "Llamar",
                    iconRes = R.drawable.ic_call_contact,
                    onClick = {
                        context.uiTayDialedNumber(number = "4494487490")
                    }
                )

            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Información y Recursos",
                style = textB18,
                color = tay_red_600,
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ResourceItem(
                        title = "¿Cómo usar la app?",
                        subtitle = "Guías y tutoriales de pedidos",
                        onClick = {
                            onNavigateTo(
                                ScreenInitNav.InformationDetail(
                                    title = "¿Cómo usar la app?",
                                    content = "Bienvenido a Pizzzeria. Para realizar un pedido, simplemente navega por nuestras categorías de pizzas, personaliza tu masa y orilla con queso, añádelas al carrito y selecciona si deseas recojo en local o entrega a domicilio. ¡El pago es rápido y seguro!"
                                )
                            )
                        }
                    )
                    HorizontalDivider(color = Color(0xFFF0F2F5), thickness = 1.dp)
                    ResourceItem(
                        title = "No recuerdo mis datos",
                        subtitle = "Recuperación de acceso y cuenta",
                        onClick = {
                            onNavigateTo(
                                ScreenInitNav.InformationDetail(
                                    title = "No recuerdo mis datos",
                                    content = "Si olvidaste tu contraseña o usuario, puedes contactar a nuestro soporte a través de WhatsApp o correo electrónico para verificar tu identidad y restablecer tus credenciales de acceso de forma segura."
                                )
                            )
                        }
                    )
                    HorizontalDivider(color = Color(0xFFF0F2F5), thickness = 1.dp)
                    ResourceItem(
                        title = "Sobre Nosotros",
                        subtitle = "Conoce más de nuestra pizzería",
                        onClick = {
                            onNavigateTo(
                                ScreenInitNav.InformationDetail(
                                    title = "Sobre Nosotros",
                                    content = "Pizzzeria es la cadena líder en ofrecer las mejores pizzas artesanales con ingredientes frescos y de la más alta calidad. Nos apasiona brindar una experiencia única en cada entrega."
                                )
                            )
                        }
                    )
                    HorizontalDivider(color = Color(0xFFF0F2F5), thickness = 1.dp)
                    ResourceItem(
                        title = "Términos y Condiciones",
                        subtitle = "Políticas de servicio y uso",
                        onClick = {
                            onNavigateTo(
                                ScreenInitNav.InformationDetail(
                                    title = "Términos y Condiciones",
                                    content = "El uso de esta aplicación móvil implica la aceptación de nuestros términos y condiciones de servicio. Los pedidos están sujetos a disponibilidad de cobertura de entrega y stock en tienda."
                                )
                            )
                        }
                    )
                    HorizontalDivider(color = Color(0xFFF0F2F5), thickness = 1.dp)
                    ResourceItem(
                        title = "Privacidad y Seguridad",
                        subtitle = "Protección de datos personales",
                        onClick = {
                            onNavigateTo(
                                ScreenInitNav.InformationDetail(
                                    title = "Privacidad y Seguridad",
                                    content = "Tus datos personales y transacciones están protegidos bajo estrictos estándares de seguridad y cifrado. No compartimos tu información con terceros."
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ContactCard(
    modifier: Modifier = Modifier,
    title: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .size(64.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = title,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun ResourceItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = textB14, color = Color.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, style = textM14, color = Color.Gray)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}
