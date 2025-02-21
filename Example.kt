
@SuppressLint("RememberReturnType")
@Composable
fun ReceiptList(
    viewModel: ReceiptListViewModel,
    onClick: () -> Unit
) {
    val docs = viewModel.docs

    /** @updateDragState: is resetting the DragTargetInfo infos to prevent a position issue!  **/
    fun updateDragState(docs: List<Int>, state: DragTargetInfo) {
        state.dataToDrop = null
        state.draggableComposable = null
        state.isDragging = false
        state.dragOffset = Offset.Zero
    }
    /** @dragAndDropUpdateList: is swapping the the items in the list  **/
    fun dragAndDropUpdateList(fromId: Int, toId: Int, state: DragTargetInfo) {
        val fromIndex = docs.indexOf(fromId)
        val toIndex = docs.indexOf(toId)
        if (fromIndex != -1 && toIndex != -1) {
            val temp = docs[fromIndex]
            docs[fromIndex] = docs[toIndex]
            docs[toIndex] = temp
            updateDragState(docs, state)
        }
    }

    Column(
        modifier = Modifier
            .semantics { }
            .fillMaxHeight(0.9f)
            .background(color = colorResource(R.color.white))
            .padding(
                DesignSizeValues.TWENTY_FOUR.dp,
                DesignSizeValues.ZERO.dp,
                DesignSizeValues.TWENTY_FOUR.dp,
                DesignSizeValues.FIFTY_SIX.dp
            ),
        verticalArrangement = Arrangement.spacedBy(DesignSizeValues.SIX_TEEN.dp)
    ) {
   
        /**
         * @Info: Currently, all images move during the drag operation because they share the same
         * integer ID. The drag functionality uses this integer ID to identify each element that is
         * currently being dragged.
         */
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = {
                itemsIndexed(docs, key = { index, item -> item }) { _, item ->

                    val state = LocalDragTargetInfo.current

                    val currentZIndex = remember { mutableFloatStateOf(docs.size.toFloat()) }
                    val hapticFeedback = LocalHapticFeedback.current

                    LongPressDraggable(
                        modifier = Modifier
                            .zIndex(if (state.isDragging && state.dataToDrop == item) 100f else currentZIndex.floatValue)
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
                                dragAndDropUpdateList(item, data, state)
                            }

                            DragTarget(isActive = true, modifier = Modifier, dataToDrop = item) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
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


        if (viewModel.showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.showDeleteDialog = false },
                title = { Text(text = "Dokument löschen") },
                text = { Text(text = "Sind Sie sicher, dass Sie das Dokument endgültigt entfernen möchten?") },
                containerColor = colorResource(R.color.white),
                confirmButton = {
                    TextButton(onClick = { viewModel.showDeleteDialog = false }) {
                        Text("Löschen")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.showDeleteDialog = false }) {
                        Text("Abbrechen")
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun ReceiptListPreview() {
    ReceiptList(
        viewModel = ReceiptListViewModel(),
        onClick = {}
    )
}