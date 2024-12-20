package com.example.sneakershopstorage.screens

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.sneakershopstorage.model.getImagesUrls
import com.example.sneakershopstorage.viewmodels.ShoeViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.currentKoinScope

@Composable
fun ShoeScreen(modifier: Modifier = Modifier, viewModel: ShoeViewModel = koinViewModel(scope = currentKoinScope())) {
    val scrollState = rememberScrollState()
    val shoe by viewModel.shoe.collectAsState()
    var imageUrls by remember {
        mutableStateOf(emptyList<String>())
    }

    LaunchedEffect(shoe) {
        imageUrls = shoe?.getImagesUrls() ?: emptyList()
    }

    shoe?.let {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight()
        ) {
            Row(
                modifier = Modifier
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
                    .fillMaxWidth()
                    .scrollable(scrollState, Orientation.Vertical),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AsyncImage(
                    model = imageUrls[0],
                    contentDescription = "Image of shoe"
                )

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
                    if(it.sizes.isNotEmpty()) {
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