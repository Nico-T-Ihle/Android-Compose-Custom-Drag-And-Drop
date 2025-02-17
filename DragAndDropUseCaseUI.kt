    fun dragAndDropUpdateList(fromId: Int, toId: Int) {
        println("ListSt: $docs")
        println("FLQM $fromId, $toId")
        
        val fromIndex = docs.indexOf(fromId)
        val toIndex = docs.indexOf(toId)

        if (fromIndex != -1 && toIndex != -1) {
            // Tausche die Elemente in der Liste
            val temp = docs[fromIndex]
            docs[fromIndex] = docs[toIndex]
            docs[toIndex] = temp
        }
        println("List: $docs")
    }

  
  LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = {
                itemsIndexed(docs) { index, item ->
                    val currentZIndex = remember { mutableFloatStateOf(2f) }
                    val hapticFeedback = LocalHapticFeedback.current

                    LongPressDraggable(
                        modifier = Modifier
                            .zIndex(currentZIndex.floatValue)
                            .pointerInput(item) {
                                detectTapGestures(
                                    onLongPress = {
                                        println("Item long-pressed: $item at index: $index")
                                    }
                                )
                            }
                            .fillMaxSize(),
                        getDataType = 1,
                        item = item,
                        zIndex = currentZIndex
                    ) {
                        DropTarget(
                            getDataType = 0,
                            item = item,
                            modifier = Modifier,
                        ) { isInBound, data ->

                            if (isInBound) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            }

                            data?.let {
                                dragAndDropUpdateList(item, data)
                            }

                            DragTarget(modifier = Modifier, dataToDrop = item) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth()
                                        .background(if(isInBound) {Color.Green} else {Color.Red}  )
                                ) {
                                    ReceiptElement(
                                        image = painterResource(id = item),
                                        additionalElementsCount = 0,
                                        modifier = Modifier,
                                        removable = viewModel.documentEditMode,
                                        onRemove = {
                                            viewModel.showDeleteDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )