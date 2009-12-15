package griffon.slideware

// import griffon.transitions.TransitionLayout
import com.bric.image.transition.Transition2D
import com.bric.image.transition.spunk.SwivelTransition2D
import net.miginfocom.swing.MigLayout
import java.awt.event.MouseEvent
import java.awt.Font

createFooter = { idx ->
	def footer
	noparent {
		footer = panel(layout: new MigLayout("fill","[right]1%","[bottom]")) {
			label(idx.toString(), cssClass: 'footer')
		}
	}
	footer
}

createHeader = { str ->
    def header
    noparent {
        if(str == null) {
            header = panel()
        } else {
            header = panel(layout: new MigLayout("fill","2%[center]2%","[center]")) {
                label(text: str, cssClass: 'header')
			}
        }
    }
    header
}

try {
	build(getClass().classLoader.loadClass("SlideDefaults"))
} catch(Exception x) {
	// ignore
}

lookupPopup = jidePopup(movable: false, resizable: false, border: emptyBorder(0),
	popupMenuWillBecomeInvisible: {evt -> doLater{deckFrame.requestFocus()} } ) {
	panel {
		borderLayout()
		textField(id: "input", columns: 2, editable: true,
			border: emptyBorder(0),
			font: new Font("SansSerif", Font.BOLD, 64))
	}
}
lookupPopup.setDefaultFocusComponent(input)
keyStrokeAction(component: input,
	keyStroke: "ENTER",
	action: jumpAction)

lafsPopup = jidePopup(movable: false, resizable: false, border: emptyBorder(0),
	popupMenuWillBecomeInvisible: {evt -> doLater{deckFrame.requestFocus()} } ) {
	scrollPane {
		list(id: "lafList", visibleRowCount: 10,
			border: emptyBorder(0),
			font: new Font("SansSerif", Font.BOLD, 24),
			listData: model.lafs.keySet() as Object[])
	}
}
lafsPopup.setDefaultFocusComponent(lafList)
keyStrokeAction(component: lafList,
	keyStroke: "ENTER",
	action: lafAction)

handleMouseEvent = { evt ->
	if(evt.button == MouseEvent.BUTTON1) {
		deck.layout.next(deck)
	} else if(evt.button == MouseEvent.BUTTON3) {
		deck.layout.previous(deck)
	}
}

def transitionLayout = getClass().classLoader.loadClass("griffon.transitions.TransitionLayout").newInstance()
bean(transitionLayout, defaultDuration: 1500L,
	defaultTransition: new SwivelTransition2D(Transition2D.CLOCKWISE))

application(title: app.config.application.title,
	id: "deckFrame", undecorated: true, pack: false,
	locationByPlatform: false, location: [0,0], size: [1024, 768]) {
	borderLayout()
	panel(id: "deck", constraints: context.CENTER, layout: transitionLayout) {
		boolean building = true
		count = 0
		while(building) {
			def slideClass = null
			try {
				slideClass = getClass().classLoader.loadClass("Slide${(count++)}")
			} catch(Exception x) {
				// println x
				building = false
			}
			if(building){
				slide = build(slideClass)
				widget(slide, constraints: [name: "page${count}".toString(), transition: slide.transition],
					mouseClicked: handleMouseEvent, header: createHeader(slide.title), footer: createFooter(count))
			}
		}
	}

	keyStrokeAction(component: deck,
		keyStroke: shortcut("LEFT"),
		condition: "in focused window",
		action: previousAction)
	keyStrokeAction(component: deck,
		keyStroke: shortcut("RIGHT"),
		condition: "in focused window",
		action: nextAction)
	keyStrokeAction(component: deck,
		keyStroke: "ESCAPE",
		condition: "in focused window",
		action: closeAction)
	keyStrokeAction(component: deck,
		keyStroke: shortcut("UP"),
		condition: "in focused window",
		action: lookupAction)
	keyStrokeAction(component: deck,
		keyStroke: shortcut("shift F"),
		condition: "in focused window",
		action: showLafsAction)

	swingRepaintTimeline(deck, loop: true)
}