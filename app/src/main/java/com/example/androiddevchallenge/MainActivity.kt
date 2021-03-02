/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.example.androiddevchallenge.data.Puppy
import com.example.androiddevchallenge.data.puppyList
import com.example.androiddevchallenge.ui.theme.MyTheme

class MainActivity : AppCompatActivity() {

    private val navigationViewModel by viewModels<NavigationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp(navigationViewModel)
            }
        }
    }

    override fun onBackPressed() {
        if (!navigationViewModel.onBack()) {
            super.onBackPressed()
        }
    }
}

// Start building your app here!
@Composable
fun MyApp(
    navigationViewModel: NavigationViewModel
) {
    Crossfade(targetState = navigationViewModel.currentScreen) { screen ->
        Surface(color = MaterialTheme.colors.background) {
            when (screen) {
                is Screen.PuppyList -> PuppyListScreen(
                    puppyList,
                    navigateTo = navigationViewModel::navigateTo
                )
                is Screen.PuppyDetail -> PuppyDetailScreen(
                    puppyId = screen.puppyId,
                    onBack = { navigationViewModel.onBack() }
                )
            }
        }
    }
}

@Composable
fun PuppyListScreen(
    puppyList: List<Puppy>,
    navigateTo: (Screen) -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            val title = stringResource(id = R.string.app_name)
            TopAppBar(
                title = { Text(text = title) }
            )
        },
        content = { innerPadding ->
            val modifier = Modifier.padding(innerPadding)
            PuppyListContent(puppyList = puppyList, navigateTo = navigateTo, modifier)
        }
    )
}

@Composable
fun PuppyListContent(
    puppyList: List<Puppy>,
    navigateTo: (Screen) -> Unit,
    modifier: Modifier
) {
    LazyColumn(modifier = modifier) {
        items(puppyList) { puppy ->
            PuppyCard(
                puppy = puppy,
                navigateTo = navigateTo
            )
            PuppyListDivider()
        }
    }
}

@Composable
fun PuppyCard(
    puppy: Puppy,
    navigateTo: (Screen) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = { navigateTo(Screen.PuppyDetail(puppy.id)) })
            .padding(16.dp)
    ) {
        PuppyImage(puppy, Modifier.padding(end = 16.dp))
        Column(modifier = Modifier.weight(1f)) {
            PuppyTitle(puppy)
            PuppySubTitle(puppy)
        }
    }
}

@Composable
fun PuppyTitle(puppy: Puppy) {
    Text("Name: ${puppy.name}", style = MaterialTheme.typography.subtitle1)
}

@Composable
fun PuppySubTitle(puppy: Puppy) {
    Text("Breed: ${puppy.type}", style = MaterialTheme.typography.body2)
}

@Composable
fun PuppyImage(puppy: Puppy, modifier: Modifier) {
    Image(
        painter = painterResource(puppy.imageId),
        contentDescription = null, // decorative
        modifier = modifier
            .size(100.dp, 100.dp)
            .clip(MaterialTheme.shapes.medium)
    )
}

@Composable
fun PuppyListDivider() {
    Divider(
        modifier = Modifier.padding(horizontal = 14.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
    )
}

@Composable
fun PuppyDetailScreen(
    puppyId: String,
    onBack: () -> Unit
) {
    val puppy = puppyList.find { it.id == puppyId }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = puppy!!.name)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                }
            )
        },
        content = { innerPadding ->
            val modifier = Modifier.padding(innerPadding)
            PuppyDetailContent(puppy, modifier)
        }
    )
}

val defaultSpacerSize = 16.dp

@Composable
fun PuppyDetailContent(puppy: Puppy?, modifier: Modifier) {
    LazyColumn(modifier = modifier.padding(horizontal = defaultSpacerSize)) {
        item {
            Spacer(Modifier.height(defaultSpacerSize))
            PuppyHeaderImage(puppy)
        }
        item {
            Text(text = "Meet ${puppy!!.name}", style = MaterialTheme.typography.h4)
            Spacer(Modifier.height(8.dp))
        }
        item {
            Text(text = puppy!!.desc, style = MaterialTheme.typography.body1)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun PuppyHeaderImage(puppy: Puppy?) {
    val imageModifier = Modifier
        .heightIn(min = 180.dp)
        .fillMaxWidth()
        .clip(shape = MaterialTheme.shapes.medium)
    Image(
        painter = painterResource(puppy!!.headerImgId),
        contentDescription = null, // decorative
        modifier = imageModifier,
        contentScale = ContentScale.Crop
    )
    Spacer(Modifier.height(defaultSpacerSize))
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp(NavigationViewModel(SavedStateHandle()))
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp(NavigationViewModel(SavedStateHandle()))
    }
}
