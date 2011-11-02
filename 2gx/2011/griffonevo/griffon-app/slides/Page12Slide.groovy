import com.bric.image.transition.Transition2D
import com.bric.image.transition.spunk.*
import com.bric.image.transition.vanilla.*

slide(footer: createFooter(0), title: '', backgroundPainter: griffonPainter.curry('present'), transition: new DiamondsTransition2D(120)) {
    migLayout(layoutConstraints: 'fill',
              columnConstraints: '5%[center]5%',
              rowConstraints: '5%[top]5%[bottom]5%')
    label('PRESENT', cssClass: 'H1o', constraints: 'wrap')
    label(GRIFFON_IMG_CONF.present.url, constraints: 'right', foreground: Color.WHITE)
}
