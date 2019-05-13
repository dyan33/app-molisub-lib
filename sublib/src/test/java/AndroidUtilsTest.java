
public class AndroidUtilsTest {

    public static void main(String[] args) {


        String runScript = "class MyTouch{\n" +
                "\n" +
                "    constructor(el){\n" +
                "        this.el=el;\n" +
                "\n" +
                "        var rect = this.el.getBoundingClientRect();\n" +
                "\n" +
                "        this.x=rect.left + rect.width/2.0;\n" +
                "        this.y=rect.top + rect.height/2.0;\n" +
                "\n" +
                "        log(\"click\",\"x:\"+this.x+\" y:\"+this.y)\n" +
                "    }\n" +
                "\n" +
                "    touchStart(){\n" +
                "\n" +
                "        var touch = new Touch({\n" +
                "            identifier:0,\n" +
                "            target: this.el,\n" +
                "            clientX: this.x,\n" +
                "            clientY: this.y,\n" +
                "            pageX:this.x,\n" +
                "            pageY:this.y,\n" +
                "            screenX: this.x,\n" +
                "            screenY: this.y,\n" +
                "            radiusX: 25.509714126586914,\n" +
                "            radiusY: 25.509714126586914,\n" +
                "            rotationAngle: 0,\n" +
                "            force: 0.6500000357627869\n" +
                "        });\n" +
                "    \n" +
                "        var touchEvent = new TouchEvent(\"touchstart\", {\n" +
                "            cancelable: false,\n" +
                "            bubbles: true,\n" +
                "            touches: [touch],\n" +
                "            targetTouches: [],\n" +
                "            changedTouches: [touch]\n" +
                "        });\n" +
                "        this.el.dispatchEvent(touchEvent);\n" +
                "    }\n" +
                "\n" +
                "    touchEnd(){\n" +
                "        var touch = new Touch({\n" +
                "            identifier:0,\n" +
                "            target: this.el,\n" +
                "            clientX: this.x,\n" +
                "            clientY: this.y,\n" +
                "            pageX:this.x,\n" +
                "            pageY:this.y,\n" +
                "            screenX: this.x,\n" +
                "            screenY: this.y,\n" +
                "            radiusX: 25.509714126586914,\n" +
                "            radiusY: 25.509714126586914,\n" +
                "            rotationAngle: 0,\n" +
                "            force: 0,\n" +
                "        });\n" +
                "    \n" +
                "        var touchEvent = new TouchEvent(\"touchend\", {\n" +
                "            cancelable: false,\n" +
                "            bubbles: true,\n" +
                "            touches: [touch],\n" +
                "            targetTouches: [],\n" +
                "            changedTouches: [touch]\n" +
                "        });\n" +
                "        this.el.dispatchEvent(touchEvent);\n" +
                "    }\n" +
                "\n" +
                "    click(){\n" +
                "        this.el.click()\n" +
                "    }\n" +
                "    \n" +
                "    run(){\n" +
                "\n" +
                "        this.touchStart()\n" +
                "\n" +
                "        setTimeout(() => {\n" +
                "          \n" +
                "            this.touchEnd()\n" +
                "\n" +
                "            setTimeout(() => {\n" +
                "\n" +
                "                 this.click()\n" +
                "\n" +
                "            }, 211);\n" +
                "\n" +
                "        }, 512);\n" +
                "    }\n" +
                "\n" +
                "}\n" +
                "\n" +
                "\n" +
                "\n" +
                "$(document).ready(function(){\n" +
                "        \n" +
                "            var btn1 = document.getElementById('btn_continuar');\n" +
                "            var btn2 = document.getElementById('btn_popup_der');\n" +
                "\n" +
                "            //点击一次图片\n" +
                "            setTimeout(() => {new MyTouch(img).run()}, 2345);\n" +
                "            \n" +
                "            setTimeout(() => {\n" +
                "\n" +
                "                log(\"step1\",\"\")\n" +
                "                new MyTouch(btn1).run();\n" +
                "\n" +
                "                setTimeout(() => {\n" +
                "\n" +
                "                      log(\"step2\",\"\")\n" +
                "                      new MyTouch(btn2).run()\n" +
                "\n" +
                "                }, 1520)\n" +
                "\n" +
                "        }, 5200);\n" +
                "        \n" +
                "});";


        System.out.println(runScript);
    }
}
