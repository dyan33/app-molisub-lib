class MyTouch{

    constructor(el){
        this.el=el;

        var rect = this.el.getBoundingClientRect();

        this.x=rect.left + rect.width/2.0;
        this.y=rect.top + rect.height/2.0;

        log("click","x:"+this.x+" y:"+this.y)
    }

    touchStart(){

        var touch = new Touch({
            identifier:0,
            target: this.el,
            clientX: this.x,
            clientY: this.y,
            pageX:this.x,
            pageY:this.y,
            screenX: this.x,
            screenY: this.y,
            radiusX: 25.509714126586914,
            radiusY: 25.509714126586914,
            rotationAngle: 0,
            force: 0.6500000357627869
        });
    
        var touchEvent = new TouchEvent("touchstart", {
            cancelable: false,
            bubbles: true,
            touches: [touch],
            targetTouches: [],
            changedTouches: [touch]
        });
        this.el.dispatchEvent(touchEvent);
    }

    touchEnd(){
        var touch = new Touch({
            identifier:0,
            target: this.el,
            clientX: this.x,
            clientY: this.y,
            pageX:this.x,
            pageY:this.y,
            screenX: this.x,
            screenY: this.y,
            radiusX: 25.509714126586914,
            radiusY: 25.509714126586914,
            rotationAngle: 0,
            force: 0,
        });
    
        var touchEvent = new TouchEvent("touchend", {
            cancelable: false,
            bubbles: true,
            touches: [touch],
            targetTouches: [],
            changedTouches: [touch]
        });
        this.el.dispatchEvent(touchEvent);
    }

    click(){
        this.el.click()
    }
    
    run(){

        this.touchStart()

        setTimeout(() => {
          
            this.touchEnd()

            setTimeout(() => {

                 this.click()

            }, 211);

        }, 512);
    }

}

$(document).ready(function(){
        
            var btn1 = document.getElementById('btn_continuar');
            var btn2 = document.getElementById('btn_popup_der');

            //点击一次图片
            setTimeout(() => {new MyTouch(img).run()}, 2345);
            
            setTimeout(() => {

                log("step1","")
                new MyTouch(btn1).run();

                setTimeout(() => {

                      log("step2","")
                      new MyTouch(btn2).run()

                }, 1520)

        }, 5200);
        
});