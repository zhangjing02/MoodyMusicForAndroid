package com.example.moodymusicforandroid.ui.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moodymusicforandroid.data.model.AlbumSocialContent

/**
 * 社区交流展示区域组件
 *
 * 负责在音乐库页面展示用户的动态、评论以及回复输入框。
 * 处理了加载错误和数据呈现的逻辑。
 */
@Composable
fun CommunitySocialSection(
    /** 社交内容数据（可能为空） */
    content: AlbumSocialContent?,
    /** 错误信息（如果加载失败） */
    errorMessage: String?,
    /** 绑定的输入框文本状态 */
    commentText: String,
    /** 输入框文本变更回调 */
    onCommentTextChange: (String) -> Unit,
    /** 点击重试按钮的回调 */
    onRetryClick: () -> Unit,
    /** 点击发送按钮的回调 */
    onSendClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("社区交流", style = MaterialTheme.typography.titleLarge)
        
        if (errorMessage != null && content == null) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
                Button(onClick = onRetryClick) {
                    Text("重试")
                }
            }
        }
        
        content?.let { socialContent ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(socialContent.content, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("来自: ${socialContent.author.username}", style = MaterialTheme.typography.labelSmall)
                    Text(socialContent.createdAt, style = MaterialTheme.typography.labelSmall)
                }
            }
            
            socialContent.replies.forEach { reply ->
                Card(modifier = Modifier.fillMaxWidth().padding(start = 32.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(reply.content, style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("来自: ${reply.author.username}", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = onCommentTextChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("说点什么...") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onSendClick) {
                    Text("发送")
                }
            }
        }
    }
}
