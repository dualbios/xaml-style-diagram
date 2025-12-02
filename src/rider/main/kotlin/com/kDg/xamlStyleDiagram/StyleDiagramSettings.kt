package com.kDg.xamlStyleDiagram

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service

@State(
    name = "StyleDiagramSettings",
    storages = [Storage("XamlStyleDiagram-settings.xml")]
)
@Service
class StyleDiagramSettings : PersistentStateComponent<StyleDiagramSettings.State> {

    data class State(
        var umlParams: String = "!theme amiga\n" +
            "skinparam dpi 150",
        var plantUmlServerUrl: String = "https://www.plantuml.com/plantuml",
        var isClassDiagram: Boolean = false,
    )

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    // Зручні геттери/сеттери
    var umlParams: String
        get() = myState.umlParams
        set(value) {
            myState.umlParams = value
        }
    var plantUmlServerUrl: String
        get() = myState.plantUmlServerUrl
        set(value) {
            myState.plantUmlServerUrl = value
        }

    var isClassDiagram: Boolean
        get() = myState.isClassDiagram
        set(value) {
            myState.isClassDiagram = value
        }

    companion object {
        fun getInstance(): StyleDiagramSettings = service()
    }
}
