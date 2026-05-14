import { createClient, type SupabaseClient } from '@supabase/supabase-js'

let supabaseClient: SupabaseClient | null = null
let missingConfigWarned = false

export function useSupabaseClient() {
  // No crear cliente en SSR
  if (typeof window === 'undefined') {
    return null as any
  }

  if (supabaseClient) {
    return supabaseClient
  }

  const config = useRuntimeConfig()
  const supabaseUrl = config.public.SUPABASE_URL
  const supabaseAnonKey = config.public.SUPABASE_ANON_KEY

  if (!supabaseUrl || !supabaseAnonKey) {
    if (!missingConfigWarned) {
      console.warn('Supabase no esta configurado. Define NUXT_PUBLIC_SUPABASE_URL y NUXT_PUBLIC_SUPABASE_ANON_KEY.')
      missingConfigWarned = true
    }
    return null as any
  }

  supabaseClient = createClient(supabaseUrl, supabaseAnonKey, {
    auth: {
      persistSession: false,
      autoRefreshToken: false,
      detectSessionInUrl: false
    }
  })

  return supabaseClient
}
