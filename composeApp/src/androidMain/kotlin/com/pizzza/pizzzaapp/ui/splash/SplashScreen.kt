package com.pizzza.pizzzaapp.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pizzza.pizzzaapp.core.ui.R
import com.pizzza.pizzzaapp.ui.AppViewModel
import com.pizzza.pizzzaapp.feature.auth.AuthViewModel
import com.valu.uitaycompose.swipe.UiTayGif
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textGabbiB28
import org.koin.compose.viewmodel.koinViewModel
import com.valu.uitaycompose.utils.textGabbiB35
import com.valu.uitaycompose.utils.textSe16
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(
    viewModel: AppViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel(),
    onFinished: (Boolean) -> Unit
) {
    var error by remember { mutableStateOf<String?>(null) }
    val scale = remember { Animatable(0.6f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        
        delay(500.milliseconds)
        viewModel.syncProducts(
            onComplete = { success ->
                if (success) {
                    authViewModel.checkExistingUser { role ->
                        onFinished(role?.equals("CLIENTE", ignoreCase = true) == true || role?.equals("ADMIN", ignoreCase = true) == true)
                    }
                } else {
                    error = "Error al conectar con el servidor. Revisa tu conexión."
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Image(painter = painterResource(R.drawable.ic_logo_m_pizzzeria)
            , contentDescription = "null",modifier = Modifier.width(250.dp).height(200.dp))
            
            Spacer(Modifier.height(16.dp))

            Text(
                text = "PIZZZERIA",
                color =tay_red_600,
                style = textGabbiB35,
                modifier = Modifier.scale(scale.value)
            )
            
            Spacer(Modifier.height(40.dp))

            if (error == null) {
                CircularProgressIndicator(
                    color = tay_red_600,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    text = "Cargando menú delicioso...",
                    style = textSe16,
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else {
                Text(
                    text = error!!,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 40.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = {
                        error = null
                        viewModel.syncProducts(onComplete = { success ->
                            if (success) {
                                authViewModel.checkExistingUser { role ->
                                    onFinished(role?.equals("CLIENTE", ignoreCase = true) == true || role?.equals("ADMIN", ignoreCase = true) == true)
                                }
                            }
                            else error = "Reintento fallido. Verifica tu red."
                        })
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White, 
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Reintentar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
