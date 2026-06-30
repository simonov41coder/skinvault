package com.simonov.skinvault

import io.wispforest.owo.ui.base.BaseOwoScreen
import io.wispforest.owo.ui.component.UIComponents
import io.wispforest.owo.ui.container.UIContainers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.*
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.Minecraft

class SkinVaultScreen(parent: Screen?) : BaseOwoScreen<FlowLayout>() {

    private val skinList = mutableListOf<SkinStorage.SkinEntry>()
    private var isFormOpen = false
    private var editingIndex: Int? = null
    private var searchQuery = ""

    private lateinit var contentContainer: FlowLayout
    private lateinit var listContainer: FlowLayout
    private lateinit var popupFrameContainer: FlowLayout
    private lateinit var popupModal: FlowLayout
    private lateinit var searchBarRef: io.wispforest.owo.ui.component.TextBoxComponent
    private lateinit var popupNameField: io.wispforest.owo.ui.component.TextBoxComponent
    private lateinit var popupUrlField: io.wispforest.owo.ui.component.TextBoxComponent
    private lateinit var popupConfirmBtn: FlowLayout

    private companion object {
        const val PANEL_BG    = 0xFF1A1A1A.toInt()
        const val POPUP_BG    = 0xFF242424.toInt()
        const val NAVY_ACCENT = 0xFF3A52C8.toInt()
        const val RED_ACCENT  = 0xFF8B2020.toInt()
        const val TEXT_COLOR  = 0xFFF2F2F2.toInt()
        const val DIM_COLOR   = 0xFFAAAAAA.toInt()
        const val MAX_CHARS   = 200
        const val PANEL_W     = 90
        const val BUTTON_H    = 22
        const val ICON_BTN_W  = 24
        const val ICON_SIZE   = 16
        const val GAP         = 6

        fun tex(name: String) = Identifier.fromNamespaceAndPath("skinvault", "textures/gui/$name")
    }

    override fun createAdapter(): OwoUIAdapter<FlowLayout> =
        OwoUIAdapter.create(this, UIContainers::verticalFlow)

    override fun build(rootComponent: FlowLayout) {
        rootComponent
            .surface(Surface.VANILLA_TRANSLUCENT)
            .horizontalAlignment(HorizontalAlignment.CENTER)
            .verticalAlignment(VerticalAlignment.CENTER)
            .padding(Insets.of(16))

        val titleBar = UIContainers.horizontalFlow(Sizing.fill(PANEL_W), Sizing.fixed(BUTTON_H + 8))
            .surface(Surface.flat(PANEL_BG))
            .padding(Insets.of(6, 10, 6, 10))
            .horizontalAlignment(HorizontalAlignment.CENTER)
            .verticalAlignment(VerticalAlignment.CENTER) as FlowLayout
        titleBar.margins(Insets.bottom(GAP))
        titleBar.child(
            UIComponents.label(Component.literal("Skin Vault"))
                .color(Color.ofArgb(TEXT_COLOR))
        )

        contentContainer = UIContainers.verticalFlow(Sizing.fill(PANEL_W), Sizing.fill(70))
            .surface(Surface.flat(PANEL_BG))
            .padding(Insets.of(10))
            .horizontalAlignment(HorizontalAlignment.CENTER)
            .verticalAlignment(VerticalAlignment.TOP) as FlowLayout

        val searchRow = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.fixed(BUTTON_H))
            .verticalAlignment(VerticalAlignment.CENTER) as FlowLayout
        searchRow.margins(Insets.bottom(GAP))

        searchBarRef = UIComponents.textBox(Sizing.expand())
        searchBarRef.setHint(Component.literal("Search skins...")) // Fixed placeholder issue here
        searchBarRef.setMaxLength(MAX_CHARS)
        searchBarRef.margins(Insets.right(GAP))

        val searchBtn = createIconButton("icon_search.png", NAVY_ACCENT) { doSearch() }
        searchBtn.margins(Insets.right(GAP))

        searchRow.child(searchBarRef)
        searchRow.child(searchBtn)
        searchRow.child(createIconButton("icon_plus.png", NAVY_ACCENT) { openPopup(editIndex = null) })
        contentContainer.child(searchRow)

        popupFrameContainer = UIContainers.verticalFlow(Sizing.fill(100), Sizing.content()) as FlowLayout
        contentContainer.child(popupFrameContainer)
        buildPopupModal()

        listContainer = UIContainers.verticalFlow(Sizing.fill(100), Sizing.content())
            .horizontalAlignment(HorizontalAlignment.LEFT) as FlowLayout

        val scrollWrapper = UIContainers.verticalScroll(Sizing.fill(100), Sizing.fill(60), listContainer)
        scrollWrapper.margins(Insets.top(GAP))
        contentContainer.child(scrollWrapper)

        val bottomBar = UIContainers.horizontalFlow(Sizing.fill(PANEL_W), Sizing.fixed(BUTTON_H + 4))
            .verticalAlignment(VerticalAlignment.CENTER) as FlowLayout
        bottomBar.margins(Insets.top(GAP))

        val urlPanel = UIContainers.horizontalFlow(Sizing.expand(), Sizing.fill(100))
            .surface(Surface.flat(PANEL_BG))
            .padding(Insets.of(4, 8, 4, 8))
            .verticalAlignment(VerticalAlignment.CENTER) as FlowLayout
        urlPanel.child(
            UIComponents.label(Component.literal("skinvault://repo.local"))
                .color(Color.ofArgb(DIM_COLOR))
        )

        val closeBtn = createFlatButton("Close", Sizing.fixed(60), Sizing.fill(100), NAVY_ACCENT) { this.onClose() }
        closeBtn.margins(Insets.left(GAP))

        bottomBar.child(urlPanel)
        bottomBar.child(closeBtn)

        rootComponent.child(titleBar)
        rootComponent.child(contentContainer)
        rootComponent.child(bottomBar)

        skinList.addAll(SkinStorage.load())
        refreshSkinButtons()
    }

    override fun onClose() {
        SkinStorage.save(skinList)
        super.onClose()
    }

    override fun keyPressed(input: KeyEvent): Boolean {
        if ((input.key() == 257 || input.key() == 335) && searchBarRef.isFocused) {
            doSearch()
            return true
        }
        return super.keyPressed(input)
    }

    private fun doSearch() {
        searchQuery = searchBarRef.value.trim()
        refreshSkinButtons()
    }

    private fun buildPopupModal() {
        popupModal = UIContainers.verticalFlow(Sizing.fill(100), Sizing.content())
            .surface(Surface.flat(POPUP_BG))
            .padding(Insets.of(10))
            .horizontalAlignment(HorizontalAlignment.CENTER) as FlowLayout
        popupModal.margins(Insets.bottom(GAP))

        popupNameField = UIComponents.textBox(Sizing.fill(100))
        popupNameField.setHint(Component.literal("Skin name...")) // Fixed placeholder issue here
        popupNameField.setMaxLength(MAX_CHARS)

        popupUrlField = UIComponents.textBox(Sizing.fill(100))
        popupUrlField.setHint(Component.literal("Skin URL...")) // Fixed placeholder issue here
        popupUrlField.setMaxLength(MAX_CHARS)

        val confirmRow = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.fixed(BUTTON_H))
            .horizontalAlignment(HorizontalAlignment.RIGHT) as FlowLayout
        confirmRow.margins(Insets.top(GAP))

        val cancelBtn = createFlatButton("Cancel", Sizing.fixed(56), Sizing.fixed(BUTTON_H), NAVY_ACCENT) {
            closePopup()
        }
        cancelBtn.margins(Insets.right(GAP))

        popupConfirmBtn = createFlatButton("Add Skin", Sizing.expand(), Sizing.fixed(BUTTON_H), NAVY_ACCENT) {
            val name = popupNameField.value.trim()
            val url  = popupUrlField.value.trim()
            if (name.isNotEmpty() && url.isNotEmpty()) {
                val entry = SkinStorage.SkinEntry(name, url)
                val idx = editingIndex
                if (idx != null) skinList[idx] = entry else skinList.add(entry)
                SkinStorage.save(skinList)
                searchQuery = ""
                searchBarRef.value = ""
                closePopup()
                refreshSkinButtons()
            }
        }

        confirmRow.child(cancelBtn)
        confirmRow.child(popupConfirmBtn)
        popupModal.child(popupNameField.margins(Insets.bottom(GAP)))
        popupModal.child(popupUrlField)
        popupModal.child(confirmRow)
    }

    private fun openPopup(editIndex: Int?) {
        editingIndex = editIndex
        if (editIndex != null) {
            val skin = skinList[editIndex]
            popupNameField.value = skin.name
            popupUrlField.value  = skin.url
            popupConfirmBtn.clearChildren()
            popupConfirmBtn.child(UIComponents.label(Component.literal("Save")).color(Color.ofArgb(TEXT_COLOR)))
        } else {
            popupNameField.value = ""
            popupUrlField.value  = ""
            popupConfirmBtn.clearChildren()
            popupConfirmBtn.child(UIComponents.label(Component.literal("Add Skin")).color(Color.ofArgb(TEXT_COLOR)))
        }
        isFormOpen = true
        popupFrameContainer.clearChildren()
        popupFrameContainer.child(popupModal)
    }

    private fun closePopup() {
        isFormOpen = false
        editingIndex = null
        popupFrameContainer.clearChildren()
    }

    private fun refreshSkinButtons() {
        listContainer.clearChildren()

        val filtered = if (searchQuery.isEmpty()) skinList.mapIndexed { i, s -> i to s }
                       else skinList.mapIndexed { i, s -> i to s }.filter { (_, s) -> s.name.contains(searchQuery, ignoreCase = true) }

        if (filtered.isEmpty()) {
            listContainer.child(
                UIComponents.label(
                    Component.literal(if (searchQuery.isEmpty()) "No skins saved yet." else "No results for \"$searchQuery\".")
                ).color(Color.ofArgb(DIM_COLOR))
            )
            return
        }

        for ((index, skin) in filtered) {
            val row = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.fixed(BUTTON_H))
                .surface(Surface.flat(PANEL_BG))
                .verticalAlignment(VerticalAlignment.CENTER) as FlowLayout
            row.margins(Insets.bottom(4))

            val nameBtn = createFlatButton(skin.name, Sizing.expand(), Sizing.fill(100), NAVY_ACCENT) {
                println("Loading Skin: ${skin.url}")
                sendCommand("skin url ${skin.url}")
            }
            nameBtn.margins(Insets.right(4))

            val editBtn = createIconButton("icon_edit.png", NAVY_ACCENT) { openPopup(editIndex = index) }
            editBtn.margins(Insets.right(4))

            val deleteBtn = createIconButton("icon_delete.png", RED_ACCENT) {
                skinList.removeAt(index)
                SkinStorage.save(skinList)
                refreshSkinButtons()
            }

            row.child(nameBtn)
            row.child(editBtn)
            row.child(deleteBtn)
            listContainer.child(row)
        }
    }

    private fun createFlatButton(text: String, width: Sizing, height: Sizing, baseColor: Int, onClick: () -> Unit): FlowLayout {
        var state = 0 // 0 = Idle, 1 = Hovered, 2 = Pressed

        val btn = UIContainers.horizontalFlow(width, height)
            .surface { graphics, component ->
                val renderColor = when (state) {
                    2 -> 0xFF2A45C6.toInt() // Pressed color
                    1 -> 0xFF4A65E6.toInt() // Hover color
                    else -> baseColor
                }
                Surface.flat(renderColor).draw(graphics, component)
            }
            .horizontalAlignment(HorizontalAlignment.CENTER)
            .verticalAlignment(VerticalAlignment.CENTER) as FlowLayout

        btn.child(UIComponents.label(Component.literal(text)).color(Color.ofArgb(TEXT_COLOR)))

        btn.mouseEnter().subscribe { state = 1 }
        btn.mouseLeave().subscribe { state = 0 }
        
        btn.mouseDown().subscribe { event, _ -> 
            state = 2
            onClick()
            true 
        }
        
        btn.mouseUp().subscribe { event ->
            state = 1
            true
        }

        return btn
    }

    private fun createIconButton(texture: String, color: Int, onClick: () -> Unit): FlowLayout {
        val id = tex(texture)
        var state = 0 // 0 = Idle, 1 = Hovered, 2 = Pressed

        val btn = UIContainers.horizontalFlow(Sizing.fixed(ICON_BTN_W), Sizing.fixed(BUTTON_H))
            .surface { graphics, component ->
                val renderColor = when (state) {
                    2 -> 0xFF2A45C6.toInt() // Pressed color
                    1 -> 0xFF4A65E6.toInt() // Hover color
                    else -> color
                }
                Surface.flat(renderColor).draw(graphics, component)
                
                val x = component.x() + (component.width() - ICON_SIZE) / 2
                val y = component.y() + (component.height() - ICON_SIZE) / 2
                
                graphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    id,
                    x, y,
                    0f, 0f,
                    ICON_SIZE, ICON_SIZE,
                    ICON_SIZE, ICON_SIZE
                )
            }
            .horizontalAlignment(HorizontalAlignment.CENTER)
            .verticalAlignment(VerticalAlignment.CENTER) as FlowLayout

        btn.mouseEnter().subscribe { state = 1 }
        btn.mouseLeave().subscribe { state = 0 }

        btn.mouseDown().subscribe { event, _ -> 
            state = 2
            onClick()
            true 
        }
        
        btn.mouseUp().subscribe { event ->
            state = 1
            true
        }

        return btn
    }

    private fun sendCommand(commandWithoutSlash: String) {
        val client = Minecraft.getInstance()

        if (client.getConnection() != null && client.player != null) {
            client.getConnection()?.sendCommand(commandWithoutSlash)
        }
    }
}

