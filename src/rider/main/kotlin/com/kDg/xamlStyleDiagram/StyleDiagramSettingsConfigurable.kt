package com.kDg.xamlStyleDiagram

import com.intellij.openapi.options.Configurable
    import javax.swing.*
    import java.awt.*

    class StyleDiagramSettingsConfigurable : Configurable {
        private var panel: JPanel? = null
        private var textField: JTextArea? = null
        private var plantUmlServerUrlField: JTextField? = null
        private var isClassDiagramCheckBox: JCheckBox? = null // Added field

        override fun createComponent(): JComponent? {
            val settings = StyleDiagramSettings.getInstance()
            panel = JPanel(GridBagLayout())
            val c = GridBagConstraints().apply {
                fill = GridBagConstraints.HORIZONTAL
                insets = Insets(8, 8, 8, 8)
                anchor = GridBagConstraints.NORTH // Align to top
            }

            // Label
            c.gridx = 0
            c.gridy = 0
            c.weightx = 0.0
            panel?.add(JLabel("UML Parameters:"), c)

            // Text field
            textField = JTextArea(settings.umlParams,20, 20)
            textField?.lineWrap = true
            textField?.wrapStyleWord = true
            val scrollPane = JScrollPane(textField)
            c.gridx = 1
            c.weightx = 1.0
            panel?.add(scrollPane, c)

            // Label for PlantUML Server URL
            c.gridx = 0
            c.gridy = 1
            c.weightx = 0.0
            panel?.add(JLabel("PlantUML Server URL:"), c)

            // Text box for PlantUML Server URL
            plantUmlServerUrlField = JTextField(settings.plantUmlServerUrl ?: "", 20)
            c.gridx = 1
            c.weightx = 1.0
            panel?.add(plantUmlServerUrlField, c)

            // Label for isClassDiagram
            c.gridx = 0
            c.gridy = 2
            c.weightx = 0.0
            panel?.add(JLabel("Is Class Diagram:"), c)

            // Checkbox for isClassDiagram
            isClassDiagramCheckBox = JCheckBox()
            isClassDiagramCheckBox?.isSelected = settings.isClassDiagram
            c.gridx = 1
            c.weightx = 1.0
            panel?.add(isClassDiagramCheckBox, c)

            return panel
        }

        override fun isModified(): Boolean {
            val settings = StyleDiagramSettings.getInstance()
            return settings.umlParams != textField?.text ||
                   settings.plantUmlServerUrl != plantUmlServerUrlField?.text ||
                   settings.isClassDiagram != isClassDiagramCheckBox?.isSelected
        }

        override fun apply() {
            val settings = StyleDiagramSettings.getInstance()
            settings.umlParams = textField?.text ?: ""
            settings.plantUmlServerUrl = plantUmlServerUrlField?.text ?: ""
            settings.isClassDiagram = isClassDiagramCheckBox?.isSelected ?: false
        }

        override fun getDisplayName(): String = "XAML Style Diagram Plugin Settings"
    }
