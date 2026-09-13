//
//  ActionNav.swift
//  iosApp
//
//  Created by Developer on 12/09/26.
//

enum ActionNav: Sendable{
    case uiNext
    case uimBack
    case uiDefalut
    
}


extension ActionNav: Equatable {
    public static nonisolated func == (typeGeneric: ActionNav, currentGeneric : ActionNav) -> Bool {
        switch (typeGeneric, currentGeneric) {
        case (.uiNext, .uiNext): return true
        case (.uimBack, .uimBack): return true
        default: return false
        }
    }
}
