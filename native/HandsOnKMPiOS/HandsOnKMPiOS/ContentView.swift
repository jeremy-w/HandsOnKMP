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
        Text(CommonKt.createApplicationScreenMessage())
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
