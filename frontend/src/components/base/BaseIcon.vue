<template>
  <component :is="iconComponent" :size="size" :stroke-width="strokeWidth" />
</template>

<script setup>
import { computed } from 'vue';
import {
  AlertCircle, AlertTriangle, Bell, Box, Calendar, Camera, CheckCircle,
  ChevronDown, ChevronRight, ChevronUp, ClipboardList, CreditCard,
  Edit2, Info, Lock, LogOut, Mail, MapPin, Menu, Minus, Plus,
  Search, SearchX, Settings, ShoppingCart, Store, Trash2, Trash,
  User, UserCircle, X, XCircle
} from '@lucide/vue';

const props = defineProps({
  name: {
    type: String,
    required: true,
  },
  size: {
    type: [String, Number],
    default: 24,
  },
  strokeWidth: {
    type: [String, Number],
    default: 2,
  },
});

const iconMap = {
  'alert-circle': AlertCircle, 'alert-triangle': AlertTriangle, 'bell': Bell,
  'box': Box, 'calendar': Calendar, 'camera': Camera, 'check-circle': CheckCircle,
  'chevron-down': ChevronDown, 'chevron-right': ChevronRight, 'chevron-up': ChevronUp,
  'clipboard-list': ClipboardList, 'credit-card': CreditCard, 'edit-2': Edit2,
  'info': Info, 'lock': Lock, 'log-out': LogOut, 'mail': Mail, 'map-pin': MapPin,
  'menu': Menu, 'minus': Minus, 'plus': Plus, 'search': Search, 'search-x': SearchX,
  'settings': Settings, 'shopping-cart': ShoppingCart, 'store': Store,
  'trash': Trash, 'trash-2': Trash2, 'user': User, 'user-circle': UserCircle,
  'x': X, 'x-circle': XCircle
};

const iconComponent = computed(() => {
  if (!props.name) return null;
  return iconMap[props.name] || AlertCircle;
});
</script>

<style scoped>
/* No specific styles needed here, Lucide icons are SVG and styled via props */
</style>