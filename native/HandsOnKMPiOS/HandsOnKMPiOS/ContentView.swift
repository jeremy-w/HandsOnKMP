//
//  ContentView.swift
//  HandsOnKMPiOS
//
//  Created by Jeremy W. Sherman on 2019-10-30.
//  Copyright © 2019 Jeremy W. Sherman. All rights reserved.
//

import SwiftUI
import SharedCode

struct ContentView: View {
    var body: some View {
        VStack {
            Text(CommonKt.createApplicationScreenMessage())
            Button(action: {
                NSLog("i will do the thing")
                /*
                 To actually break on this, you need to either:

                 - Use regex-break: rb createApplicationScreenMessage
                 - Use the wacky long underlying symbol name actually exposed at runtime:
                   `kfun:jeremywsherman.com.handson.kmp.createApplicationScreenMessage()kotlin.String`

                 The latter is the only way to set a persistent breakpoint using Xcode vs the LLDB console.
                 The easy way to find that name is to regex-break then copy-paste.
                 It does not seem to be in the generated Obj-C symbol table,
                 though it is amongst in the unexported text symbols that you can dump using `nm`.
                 */
                let message = CommonKt.canYouDebugMe(value: "artichoke")
                NSLog("lo, i have done the thing, and the message is: \(message)")
            }) {
                Text(NSLocalizedString("Do The Thing", comment: "button title"))
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
