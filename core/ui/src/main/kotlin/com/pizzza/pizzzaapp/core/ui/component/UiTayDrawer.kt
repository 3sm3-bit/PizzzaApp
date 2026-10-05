package com.pizzza.pizzzaapp.core.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pizzza.pizzzaapp.core.ui.R
import com.valu.uitaycompose.model.UiTayNavBarItem
import com.valu.uitaycompose.utils.tay_grey_50
import com.valu.uitaycompose.utils.tay_grey_600
import com.valu.uitaycompose.utils.tay_red_100
import com.valu.uitaycompose.utils.tay_red_50
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textSe18

@Composable
fun UiDrawer(
    items: List<UiTayNavBarItem>,
    currentActionId: Int,
    bgColor : Color,
    text : String?,
    onItemClick: (UiTayNavBarItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .height(130.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .border(width = 1.dp, color = tay_red_600, shape = CircleShape)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo_m_pizzzeria),
                        contentDescription = "ProfileAvatar",
                        modifier = Modifier.size(35.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "¡Hola, $text!",
                    style = textSe18,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = tay_red_100
        )
        Spacer(modifier = Modifier.height(8.dp))

        items.forEach { item ->
            val isSelected = item.action == currentActionId
            UiTayDrawerItem(item, isSelected,bgColor) {
                onItemClick(item)
            }
        }
    }
}

@Composable
fun UiTayDrawerItem(
    item: UiTayNavBarItem,
    selected: Boolean,
    bgColor : Color,
    onItemClick: (UiTayNavBarItem) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) tay_grey_50 else Color.Transparent)
            .clickable { onItemClick(item) }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = item.iconId),
            contentDescription = "iconItem$item.iconId"
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(item.titleId) ,
            style = TextStyle(fontSize = 16.sp),
            color = if (selected) tay_red_600 else tay_grey_600
        )
    }
}
