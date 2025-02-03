package com.example.sneakershopstorage.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

    LaunchedEffect(shoe) {
        imageUrls = shoe?.getImagesUrls() ?: emptyList()
        Log.i("Image urls", "image urls = $imageUrls")
    }

    shoe?.let {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.1f)
                    .shadow(elevation = 4.dp)
                    .background(color = Color.White),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = it.modelName,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(8.dp)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .scrollable(scrollState, Orientation.Vertical),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (imageUrls.isNotEmpty()) {
                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.4f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                        ) {
                            itemsIndexed(items = imageUrls) { index, image ->
                                AsyncImage(
                                    model = image,
                                    contentDescription = ""
                                )
                                if(index != imageUrls.size - 1)
                                Spacer(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .fillMaxHeight()
                                        .background(color = Color.Black)
                                )
                            }
                        }
                    }
                } else {
                    Log.i("Image urls", "Image urls is empty")
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.4f),
                        text = "Изображения отсутствуют",
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(
                    Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 10.dp),
                    border = BorderStroke(width = 1.dp, color = Color.Black),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Text(
                        text = "Price: " + it.price.toString(),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .align(alignment = Alignment.CenterHorizontally),
                        textAlign = TextAlign.Left
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 10.dp),
                    border = BorderStroke(width = 1.dp, color = Color.Black),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Text(
                        text = it.description,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(10.dp)
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 10.dp),
                    border = BorderStroke(width = 1.dp, color = Color.Black),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (it.sizes.isNotEmpty()) {
                            it.sizes.values.forEach { size ->
                                size.inStock.forEach { inStockInfo ->
                                    Text(
                                        text = inStockInfo.key + " : " + inStockInfo.value.toString(),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "Shoe is out of stock"
                            )
                        }
                    }
                }

            }
        }
    } ?: run {
        Column(
            modifier = Modifier
                .fillMaxSize()

        ) {
            Text(
                text = "Scan QR to see shoe information.",
                textAlign = TextAlign.Center
            )
        }
    }
}
