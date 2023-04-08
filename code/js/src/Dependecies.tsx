import { NavigationRepository } from './NavigationRepository'
import { SystemServices } from './services/SystemServices'

export const navigationRepository = new NavigationRepository()
export const systemServices = new SystemServices()

export const HOME_KEY = "Home"