//
//  BaseViewGeneral.swift
//  iosApp
//
//  Created by Developer on 12/09/26.
//

import SwiftUI
import TaySwitfUILibrary

struct BaseViewGeneral<ViewModel: BaseViewModel, Content: View>: View {
    
    @EnvironmentObject var managerAPP: PizzaManagerAPP
    @ObservedObject var viewModel: ViewModel
    private var content: Content
    @FocusState var isTextFielFicudedd: Bool
    @State private var lastInteractionDate = Date()
    @State private var optimizeKeyBoard = false
    var onDetectedError: (UiTayActionErrorFlow) -> Void = { _ in }
    
    init(viewModel: ViewModel? = nil,
         expireSession: Bool = true,
         optimizeKeyBoard : Bool = false,
         onDetectedError: @escaping (UiTayActionErrorFlow) -> Void = { _ in },
         @ViewBuilder content: () -> Content) {
        self.viewModel = viewModel ?? (BaseViewModel() as! ViewModel)
        self.onDetectedError = onDetectedError
        self.optimizeKeyBoard = optimizeKeyBoard
        self.content = content()
    }
    
    var body: some View {
        ZStack(alignment: .top) {
            content
            if viewModel.uiTayLoading || managerAPP.isLoading {
                uiTayNextView { uiTayHideKeyboard() }
                UITayLoadCircle()
            }
            
            if viewModel.uiTayError || managerAPP.hbError {
                UITayDialogoGeneric(
                    mostrar: $viewModel.uiTayError,
                    title: viewModel.uiTayErrorException.title,
                    subTitle: viewModel.uiTayErrorException.message,
                    dModle: DialogModel(
                        textBtn: "Entiendo",
                        iconName: "ic_cm_d_error"
                    )
                ) { _ in
                    onDetectedError(self.viewModel.uiTayErrorAction)
                    
                }
            }
            Color.uiTayRed1200.frame(height: 56).ignoresSafeArea(edges: .top)
        }
        .focused($isTextFielFicudedd)
        .uiTayOptimizeKeyBoard(optimizeKeyBoard)
        .onTapGesture {
            isTextFielFicudedd = false
        }
        
        .uiTayHideToolbar()
    }
    
}
