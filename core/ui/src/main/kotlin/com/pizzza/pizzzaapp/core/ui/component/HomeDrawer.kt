package com.pizzza.pizzzaapp.core.ui.component

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.pizzza.pizzzaapp.core.ui.R
import com.valu.uitaycompose.model.UiTayNavBarItem
import com.valu.uitaycompose.model.UiTayNavBarModel
import com.valu.uitaycompose.utils.tay_grey_400
import com.valu.uitaycompose.utils.tay_red_400
import com.valu.uitaycompose.utils.tay_red_600

@Composable
fun HomeDrawer(
    currentActionId: Int,
    nameUser : String?,
    onActionClick: (Int) -> Unit
) {

    ModalDrawerSheet(
        modifier = Modifier.fillMaxWidth(0.8f),
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        UiDrawer(
            items = drawerItems,
            currentActionId = currentActionId,
            bgColor = tay_red_400,
            text = nameUser
        ) { menuItem ->
            onActionClick(menuItem.action)
        }
    }
}

val drawerItems = listOf(
    UiTayNavBarItem(
        titleId = R.string.text_drawer_item_one,
        iconId = R.drawable.ic_client,
        action = 0
    ),
    UiTayNavBarItem(
        titleId = R.string.text_drawer_item_two,
        iconId = R.drawable.ic_facebook,
        action = 1
    ),
    UiTayNavBarItem(
        titleId = R.string.text_drawer_three,
        iconId = R.drawable.ic_instagram,
        action = 2
    ),
    UiTayNavBarItem(
        titleId = R.string.text_drawer_four,
        iconId = R.drawable.ic_tiktok,
        action = 3
    ),
    UiTayNavBarItem(
        titleId = R.string.text_drawer_delete_user,
        iconId = R.drawable.ic_delete_user,
        action = 4
    ),
    UiTayNavBarItem(
        titleId = R.string.text_drawer_five,
        iconId = R.drawable.ic_logout,
        action = 5
    )
)
