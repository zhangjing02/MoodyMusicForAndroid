package com.example.moodymusicforandroid.ui.classroom.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodymusicforandroid.data.model.RosterItem

@Composable
fun ClassroomSeat(
    seat: RosterItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isClaimed = seat.isClaimed == 1

    Column(
        modifier = modifier
            .width(94.dp)
            .padding(4.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFE6C690), Color(0xFFD1A86D))
                    )
                )
                .border(width = 1.dp, color = Color(0xFF9A7347), shape = RoundedCornerShape(8.dp))
        ) {
            // Seat Number (Top-Left)
            Text(
                text = seat.seatCode.takeLast(2),
                modifier = Modifier.padding(start = 7.dp, top = 5.dp),
                color = Color(0x7A4F2F16),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )

            // Seat Name (Bottom-Center)
            Text(
                text = seat.realName,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 7.dp, start = 6.dp, end = 6.dp),
                color = Color(0xFF4D2E18),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            // Occupied Overlay
            if (isClaimed) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color(0x3824406E))
                )
            }
        }

        // Legs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 14.dp)
                    .width(4.dp)
                    .height(8.dp)
                    .background(Color(0x6B4A3320)) // 0.42 alpha of shadow color
            )
            Box(
                modifier = Modifier
                    .padding(end = 14.dp)
                    .width(4.dp)
                    .height(8.dp)
                    .background(Color(0x6B4A3320))
            )
        }
    }
}
