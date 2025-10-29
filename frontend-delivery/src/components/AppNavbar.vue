<template>
  <nav class="app-navbar" :class="{ 'is-scrolled': scrolled }">
    <div class="navbar-container">
      <router-link to="/" class="navbar-brand">
        <img src="/images/logo.svg" alt="DeliveryApp Logo" class="logo" />
        <span>DeliveryApp</span>
      </router-link>

      <div class="navbar-toggle" @click="toggleMobileMenu">
        <BaseIcon :name="isMobileMenuOpen ? 'x' : 'menu'" />
      </div>

      <div :class="['navbar-links', { 'is-open': isMobileMenuOpen }]">
        <router-link to="/" class="nav-item">Início</router-link>
        
        <template v-if="authStore.isAuthenticated">
          <router-link to="/cart" class="nav-item cart-icon-wrapper">
            <BaseIcon name="shopping-cart" />
            <span v-if="cartItemCount > 0" class="cart-badge">{{ cartItemCount }}</span>
            <span class="nav-label">Carrinho</span>
          </router-link>
          <router-link v-if="authStore.isRestaurantOwner" to="/restaurant/dashboard" class="nav-item" data-testid="restaurant-dashboard-link">Meu Restaurante</router-link>
          <router-link v-if="authStore.isDeliveryUser" to="/delivery/dashboard" class="nav-item" data-testid="delivery-dashboard-link">Minhas Entregas</router-link>
          <router-link to="/orders" class="nav-item">Meus Pedidos</router-link>
          <router-link v-if="authStore.isAdmin" to="/admin/dashboard" class="nav-item">Painel Admin</router-link>
        </template>

        <div class="navbar-auth">
          <template v-if="!authStore.isAuthenticated">
            <router-link to="/login" class="nav-item">Login</router-link>
            <router-link to="/register" class="nav-item primary">
              <span>Registrar</span>
            </router-link>
          </template>
          <template v-else>
            <div class="nav-item user-profile" @click="toggleUserDropdown" data-testid="user-profile-dropdown-toggle">
              <BaseIcon name="user-circle" class="user-avatar" />
              <span class="user-name">{{ authStore.user?.nome || authStore.user?.email || 'Usuário' }}</span>
              <BaseIcon :name="isUserDropdownOpen ? 'chevron-up' : 'chevron-down'" class="dropdown-arrow" />
              <div v-if="isUserDropdownOpen" class="user-dropdown" @click.stop>
                <router-link to="/profile" class="dropdown-item" @click="closeUserDropdown" data-testid="profile-link">
                  <BaseIcon name="user" /> Perfil
                </router-link>
                <div class="dropdown-item logout" @click="logout" data-testid="logout-link">
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
import { useAuthStore } from '@/stores/auth';
import { useCartStore } from '@/stores/cart';
import BaseIcon from '@/components/base/BaseIcon.vue';

const authStore = useAuthStore();
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

const logout = () => {
  closeUserDropdown();
  authStore.logout();
  router.push('/login');
};
</script>

<style lang="scss" scoped>
.app-navbar {
  background-color: transparent;
  color: var(--color-text-dark);
  padding: var(--spacing-md) var(--spacing-lg);
  position: sticky;
  top: 0;
  z-index: 999;
  transition: background-color var(--transition-fast), box-shadow var(--transition-fast), border-bottom var(--transition-fast);
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
  transition: transform 0.2s ease;

  &:hover {
    transform: scale(1.03);
  }

  .logo {
    height: 38px;
  }
}

.navbar-toggle {
  display: none;
  font-size: var(--font-size-xl);
  cursor: pointer;

  @media (max-width: 768px) {
    display: block;
  }
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
    border-top: 1px solid var(--color-border);
    box-shadow: var(--shadow-md);
    transform: translateY(-150%);
    opacity: 0;
    pointer-events: none;
    transition: transform 0.3s ease-out, opacity 0.3s ease-out;

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
  transition: all var(--transition-fast);
  white-space: nowrap;
  font-weight: var(--font-weight-medium);
  border-radius: var(--radius-md);
  position: relative;

  &:hover {
    color: var(--color-primary);
    background-color: var(--color-primary-light-transparent, rgba(255, 87, 34, 0.1));
  }

  &.router-link-exact-active {
    color: var(--color-primary);
    font-weight: var(--font-weight-semibold);
  }

  &.primary {
    background-color: var(--color-primary);
    color: var(--color-text-light);
    padding: var(--spacing-sm) var(--spacing-lg);
    border-radius: var(--radius-full);
    font-weight: var(--font-weight-semibold);
    display: flex;
    align-items: center;
    gap: var(--spacing-sm);
    
    &:hover {
      background-color: var(--color-primary-dark);
      color: var(--color-text-light);
      transform: translateY(-2px);
      box-shadow: var(--shadow-md);
    }
  }
}

.cart-icon-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);

  .nav-label {
    display: none; // Only show on mobile
    @media (max-width: 768px) {
      display: inline;
    }
  }

  .cart-badge {
    position: absolute;
    top: 0px;
    right: 0px;
    background-color: var(--color-danger);
    color: var(--color-text-light);
    border-radius: var(--radius-full);
    font-size: 10px;
    padding: 2px 5px;
    line-height: 1;
    font-weight: var(--font-weight-bold);
    border: 2px solid var(--color-surface);
  }
}

.navbar-auth {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);

  @media (max-width: 768px) {
    flex-direction: column;
    width: 100%;
    gap: var(--spacing-sm);
    margin-top: var(--spacing-md);

    .nav-item {
      width: 90%;
      text-align: center;
      justify-content: center;
    }
  }
}

.user-profile {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
  position: relative;
  padding: var(--spacing-sm);
  border-radius: var(--radius-full);

  &:hover {
    background-color: var(--color-border);
  }

  .user-avatar {
    font-size: var(--font-size-xl);
    color: var(--color-primary);
  }

  .user-name {
    max-width: 120px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-weight: var(--font-weight-semibold);
    
    @media (max-width: 768px) {
      max-width: none;
    }
  }

  .dropdown-arrow {
    font-size: var(--font-size-sm);
    color: var(--color-text-muted);
  }

  .user-dropdown {
    position: absolute;
    top: calc(100% + var(--spacing-sm));
    right: 0;
    background-color: var(--color-surface);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-lg);
    min-width: 200px;
    overflow: hidden;
    z-index: 1000;
    border: 1px solid var(--color-border);

    .dropdown-item {
      display: flex;
      align-items: center;
      gap: var(--spacing-md);
      padding: var(--spacing-md);
      color: var(--color-text-dark);
      text-decoration: none;
      white-space: nowrap;
      font-weight: var(--font-weight-medium);
      cursor: pointer;
      transition: all var(--transition-fast);

      &:hover {
        background-color: var(--color-border);
        color: var(--color-primary);
      }

      &.logout {
        color: var(--color-danger);
        &:hover {
          background-color: var(--color-danger);
          color: var(--color-text-light);
        }
      }

      &:not(:last-child) {
        border-bottom: 1px solid var(--color-border);
      }
    }
  }
}
</style>
