//
//  PizzaManagerAPP.swift
//  iosApp
//
//  Created by Developer on 12/09/26.
//

import Foundation
internal import Combine
import SwiftUI
import TaySwitfUILibrary

final class PizzaManagerAPP: ObservableObject {
    @Published var currentScreen: ContentViewScreen? = .splash
    @Published var isLoading: Bool = false
    @Published var hbError: Bool = false
   
}

struct AppContentView: View {
    @StateObject var flowManager = PizzaManagerAPP()
    
    var body: some View {
        Group {
            switch flowManager.currentScreen {
            case .splash:
                SplashView()
            case .auth:
                NavigationStack{
                  LoginView()
                }
            case .home:
                NavigationStack{
                    HomeClientView()
                }
            case .none:
                EmptyView()
            }
        }
        .environmentObject(flowManager)
    }
}


enum ContentViewScreen: Hashable {
    case splash
    case auth
    case home
}
