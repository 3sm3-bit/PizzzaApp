package com.pizzza.pizzzaapp.feature.info

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pizzza.pizzzaapp.core.navigation.ScreenInitNav
import com.pizzza.pizzzaapp.core.ui.R
import com.pizzza.pizzzaapp.core.ui.utils.openWhatsApp
import com.valu.uitaycompose.extra.UiTayCToolBar
import com.valu.uitaycompose.model.UiToolBarModel
import com.valu.uitaycompose.utils.COUNTRY_CODE_MX
import com.valu.uitaycompose.utils.extension.uiTayDialedNumber
import com.valu.uitaycompose.utils.extension.uiTayOpenEmail
import com.valu.uitaycompose.utils.tay_red_50
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textB14
import com.valu.uitaycompose.utils.textB18
import com.valu.uitaycompose.utils.textM14

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenInfoMain(
    onNavigateTo: (ScreenInitNav) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val guideHtml = androidx.compose.ui.res.stringResource(R.string.guide_how_to_use_app)
    val forgotDataHtml = androidx.compose.ui.res.stringResource(R.string.guide_forgot_data)
    val termsHtml = androidx.compose.ui.res.stringResource(R.string.guide_terms_and_conditions)

    Scaffold(
        topBar = {
            Surface(color = Color.White) {
                Box(modifier = Modifier.statusBarsPadding()) {
                    UiTayCToolBar(
                        uiTayText = "Información y Ayuda",
                        uiTayModifier = UiToolBarModel()
                            .backgroundColor(Color.White)
                            .textColor(tay_red_600)
                            .iconColor(tay_red_600)
                    ) { _ ->
                        onBack.invoke()
                    }
                }
            }
        },
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
                        context.uiTayOpenEmail(
                            email = "lapizzzeria@outlook.com",
                             subject = "Hola me gustaría consultar:",
                             body = "Hola, me gustaría realizar la siguiente consulta:"
                        )
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
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(3.dp, tay_red_50)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ResourceItem(
                        title = "¿Cómo usar la app?",
                        subtitle = "Guías y tutoriales de pedidos",
                        onClick = {
                            onNavigateTo(
                                ScreenInitNav.InformationDetail(
                                    title = "¿Cómo usar la app?",
                                    content = guideHtml
                                )
                            )
                        }
                    )
                    HorizontalDivider(color = tay_red_50, thickness = 1.dp)
                    ResourceItem(
                        title = "No recuerdo mis datos",
                        subtitle = "Recuperación de acceso y cuenta",
                        onClick = {
                            onNavigateTo(
                                ScreenInitNav.InformationDetail(
                                    title = "No recuerdo mis datos",
                                    content = forgotDataHtml
                                )
                            )
                        }
                    )
                    HorizontalDivider(color = tay_red_50, thickness = 1.dp)
                    ResourceItem(
                        title = "Sobre Nosotros",
                        subtitle = "Conoce más de nuestra pizzería",
                        onClick = {
                            onNavigateTo(
                                ScreenInitNav.InformationWebView(
                                    title = "Sobre Nosotros",
                                    url = "https://lapizzzeria.com/conoce-mas-sobre-la-pizzzeria/"
                                )
                            )
                        }
                    )
                    HorizontalDivider(color = tay_red_50, thickness = 1.dp)
                    ResourceItem(
                        title = "Términos y Condiciones",
                        subtitle = "Conoce las reglas de uso de la app",
                        onClick = {
                            onNavigateTo(
                                ScreenInitNav.InformationDetail(
                                    title = "Términos y Condiciones",
                                    content = termsHtml
                                )
                            )
                        }
                    )
                    HorizontalDivider(color = tay_red_50, thickness = 1.dp)
                    ResourceItem(
                        title = "Política de Privacidad y Seguridad",
                        subtitle = "Protección de tus datos personales",
                        onClick = {
                            onNavigateTo(
                                ScreenInitNav.InformationWebView(
                                    title = "Política de Privacidad",
                                    url = "https://lapizzzeria.com/aviso-de-privacidad/"
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
