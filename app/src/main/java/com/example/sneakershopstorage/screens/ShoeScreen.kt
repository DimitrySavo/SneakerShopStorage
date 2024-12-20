package com.example.sneakershopstorage.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.sneakershopstorage.model.getImagesUrls
import com.example.sneakershopstorage.viewmodels.ShoeViewModel

@Composable
fun ShoeScreen(modifier: Modifier = Modifier, viewModel: ShoeViewModel) {
    val scrollState = rememberScrollState()
    val shoe by viewModel.shoe.collectAsState()
    var imageUrls by remember {
        mutableStateOf(emptyList<String>())
    }

    val pagerState = rememberPagerState(pageCount = { imageUrls.size })

    LaunchedEffect(shoe) {
        imageUrls = shoe?.getImagesUrls() ?: emptyList()
        Log.i("Image urls", "image urls = $imageUrls")
    }

    shoe?.let {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 16.dp, bottom = 8.dp)
                    .shadow(elevation = 4.dp)
            ) {
                Text(
                    text = it.modelName,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .scrollable(scrollState, Orientation.Vertical),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (imageUrls.isNotEmpty()) {
                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .weight(0.4f)
                    ) {
                        HorizontalPager (
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) { page ->
                            Box (
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = imageUrls[page],
                                    contentDescription = "Image $page",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(imageUrls.size) { index ->
                                val isSelected = pagerState.currentPage == index
                                val color = if (isSelected) Color.White else Color.Gray.copy(alpha = 0.5f)
                                Box (
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .size(if (isSelected) 10.dp else 8.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                            }
                        }
                    }
                } else {
                    Log.i("Image urls", "Image urls is empty")
                }

                Text(
                    text = "Price:" + it.price.toString(),
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Description:",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = it.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(start = 8.dp)
                )

                Text(
                    text = "In stock:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (it.sizes.isNotEmpty()) {
                        it.sizes.values.forEach { size ->
                            size.inStock.forEach { inStockInfo ->
                                Text(
                                    text = inStockInfo.key + " : " + inStockInfo.value.toString(),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}