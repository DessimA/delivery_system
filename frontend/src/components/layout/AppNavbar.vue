<template>
  <nav class="app-navbar" :class="{ 'is-scrolled': scrolled }" role="navigation" aria-label="Main Navigation">
    <div class="navbar-container">
      <router-link to="/" class="navbar-brand">
        <img src="/images/logo.svg" alt="DeliveryApp Logo" class="logo" />
        <span>DeliveryApp</span>
      </router-link>

      <div class="navbar-toggle" @click="toggleMobileMenu" role="button" aria-haspopup="true" :aria-expanded="isMobileMenuOpen">
        <BaseIcon :name="isMobileMenuOpen ? 'x' : 'menu'" />
      </div>

      <div :class="['navbar-links', { 'is-open': isMobileMenuOpen }]">
        <router-link to="/" class="nav-item">Início</router-link>
        
        <template v-if="isAuthenticated">
          <router-link to="/cart" class="nav-item cart-icon-wrapper">
            <BaseIcon name="shopping-cart" />
            <span v-if="cartItemCount > 0" class="cart-badge" aria-live="polite">{{ cartItemCount }}</span>
            <span class="nav-label">Carrinho</span>
          </router-link>
          <router-link v-if="isRestaurant" to="/restaurant/dashboard" class="nav-item" data-testid="restaurant-dashboard-link">Meu Restaurante</router-link>
          <router-link v-if="isDelivery" to="/delivery/dashboard" class="nav-item" data-testid="delivery-dashboard-link">Minhas Entregas</router-link>
          <router-link to="/orders" class="nav-item">Meus Pedidos</router-link>
          <router-link v-if="isAdmin" to="/admin/dashboard" class="nav-item">Painel Admin</router-link>
        </template>

        <div class="navbar-auth">
          <template v-if="!isAuthenticated">
            <router-link to="/login" class="nav-item">Login</router-link>
            <router-link to="/register" class="nav-item primary">
              <span>Registrar</span>
            </router-link>
          </template>
          <template v-else>
            <div class="nav-item user-profile" @click="toggleUserDropdown" data-testid="user-profile-dropdown-toggle" role="button" aria-haspopup="true" :aria-expanded="isUserDropdownOpen">
              <BaseIcon name="user-circle" class="user-avatar" />
              <span class="user-name">{{ user?.name || user?.email || 'Usuário' }}</span>
              <BaseIcon :name="isUserDropdownOpen ? 'chevron-up' : 'chevron-down'" class="dropdown-arrow" />
              <div v-if="isUserDropdownOpen" class="user-dropdown" @click.stop>
                <router-link to="/profile" class="dropdown-item" @click="closeUserDropdown" data-testid="profile-link">
                  <BaseIcon name="user" /> Perfil
                </router-link>
                <div class="dropdown-item logout" @click="handleLogout" data-testid="logout-link">
                  <BaseIcon name="log-out" /> Sair
                </div>
              </div>
            </div>
          </template>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuth } from '@/composables/useAuth';
import { useCartStore } from '@/stores/cart';
import BaseIcon from '@/components/base/BaseIcon.vue';

const { user, isAuthenticated, isAdmin, isRestaurant, isDelivery, logout } = useAuth();
const cartStore = useCartStore();
const router = useRouter();

const isMobileMenuOpen = ref(false);
const isUserDropdownOpen = ref(false);
const scrolled = ref(false);

const cartItemCount = computed(() => cartStore.itemCount);

const handleScroll = () => {
  scrolled.value = window.scrollY > 10;
};

onMounted(() => {
  window.addEventListener('scroll', handleScroll);
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
  document.removeEventListener('click', handleClickOutside);
});

const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value;
};

const toggleUserDropdown = () => {
  isUserDropdownOpen.value = !isUserDropdownOpen.value;
};

const closeUserDropdown = () => {
  isUserDropdownOpen.value = false;
};

const handleClickOutside = (event) => {
  if (isUserDropdownOpen.value) {
    const userProfile = event.target.closest('.user-profile');
    if (!userProfile) {
      closeUserDropdown();
    }
  }
};

const handleLogout = () => {
  closeUserDropdown();
  logout();
  router.push('/login');
};
</script>

<style lang="scss" scoped>
/* Styles identical to original but keeping consistent with project */
.app-navbar {
  background-color: transparent;
  color: var(--color-text-dark);
  padding: var(--spacing-md) var(--spacing-lg);
  position: sticky;
  top: 0;
  z-index: 999;
  transition: all var(--transition-fast);
  border-bottom: 1px solid transparent;

  &.is-scrolled {
    background-color: var(--color-surface);
    box-shadow: var(--shadow-md);
    border-bottom: 1px solid var(--color-border);
  }
}

.navbar-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1280px;
  margin: 0 auto;
}

.navbar-brand {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  color: var(--color-primary);
  text-decoration: none;
  font-family: var(--font-headings);
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);

  .logo { height: 38px; }
}

.navbar-toggle {
  display: none;
  cursor: pointer;
  @media (max-width: 768px) { display: block; }
}

.navbar-links {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);

  @media (max-width: 768px) {
    flex-direction: column;
    position: absolute;
    top: 100%;
    left: 0;
    width: 100%;
    background-color: var(--color-surface);
    padding: var(--spacing-md) 0;
    box-shadow: var(--shadow-md);
    transform: translateY(-150%);
    opacity: 0;
    pointer-events: none;
    transition: all 0.3s ease-out;

    &.is-open {
      transform: translateY(0);
      opacity: 1;
      pointer-events: all;
    }
  }
}

.nav-item {
  color: var(--color-text-dark);
  text-decoration: none;
  padding: var(--spacing-sm) var(--spacing-md);
  font-weight: var(--font-weight-medium);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);

  &:hover {
    color: var(--color-primary);
    background-color: rgba(255, 87, 34, 0.1);
  }

  &.router-link-exact-active {
    color: var(--color-primary);
    font-weight: var(--font-weight-semibold);
  }

  &.primary {
    background-color: var(--color-primary);
    color: var(--color-text-light);
    border-radius: var(--radius-full);
    &:hover {
      background-color: var(--color-primary-dark);
      transform: translateY(-2px);
    }
  }
}

.cart-icon-wrapper {
  position: relative;
  .cart-badge {
    position: absolute;
    top: 0;
    right: 0;
    background-color: var(--color-danger);
    color: white;
    border-radius: 50%;
    font-size: 10px;
    padding: 2px 5px;
    border: 2px solid var(--color-surface);
  }
}

.user-profile {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
  position: relative;

  .user-dropdown {
    position: absolute;
    top: 100%;
    right: 0;
    background: white;
    box-shadow: var(--shadow-lg);
    border-radius: var(--radius-md);
    min-width: 180px;
    
    .dropdown-item {
      padding: var(--spacing-md);
      display: flex;
      align-items: center;
      gap: var(--spacing-sm);
      &:hover { background: var(--color-surface); }
      &.logout { color: var(--color-danger); }
    }
  }
}
</style>
