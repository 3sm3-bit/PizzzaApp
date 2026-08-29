package com.tayler.pizzzaapp.ui.roles

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.valu.uitaycompose.utils.textB20

@Composable
fun RoleSelectionScreen(
    authViewModel: com.tayler.pizzzaapp.ui.auth.AuthViewModel,
    onNavigateToClient: () -> Unit,
    onNavigateToClientHome: () -> Unit,
    onNavigateToStore: () -> Unit,
    onNavigateToDriver: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF0F2F5)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Seleccione su rol",
                style = textB20,
                color = Color(0xFF1C1E21),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            RoleButton(
                text = "Cliente",
                icon = Icons.Default.Person,
                color = Color(0xFF007BFF),
                onClick = {
                    authViewModel.checkExistingUser { role ->
                        if (role == "CLIENTE") {
                            onNavigateToClientHome()
                        } else {
                            onNavigateToClient()
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            RoleButton(
                text = "Tienda",
                icon = Icons.Default.Store,
                color = Color(0xFF10B981),
                onClick = onNavigateToStore
            )

            Spacer(modifier = Modifier.height(16.dp))

            RoleButton(
                text = "Repartidor",
                icon = Icons.Default.DirectionsCar,
                color = Color(0xFFE91E63),
                onClick = onNavigateToDriver
            )
        }
    }
}

@Composable
fun RoleButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(imageVector = icon, contentDescription = null)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}
