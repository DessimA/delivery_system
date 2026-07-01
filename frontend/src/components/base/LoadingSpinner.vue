<template>
  <div :class="['loading-spinner', `loading-spinner--${size}`]">
    <svg class="spinner" viewBox="0 0 50 50">
      <circle
        class="path"
        cx="25"
        cy="25"
        r="20"
        fill="none"
        stroke-width="5"
      ></circle>
    </svg>
  </div>
</template>

<script setup>
import { defineProps } from 'vue';

defineProps({
  size: {
    type: String,
    default: 'md', // sm, md, lg
  },
});
</script>

<style lang="scss" scoped>
.loading-spinner {
  display: inline-block;
  
  .spinner {
    animation: rotate 2s linear infinite;
    
    .path {
      stroke: currentColor; /* Usa a cor do texto do elemento pai */
      stroke-linecap: round;
      animation: dash 1.5s ease-in-out infinite;
    }
  }

  &--sm {
    width: 16px;
    height: 16px;
    .spinner {
      stroke-width: 4;
    }
  }

  &--md {
    width: 20px;
    height: 20px;
    .spinner {
      stroke-width: 5;
    }
  }

  &--lg {
    width: 24px;
    height: 24px;
    .spinner {
      stroke-width: 6;
    }
  }
}

@keyframes rotate {
  100% {
    transform: rotate(360deg);
  }
}

@keyframes dash {
  0% {
    stroke-dasharray: 1, 150;
    stroke-dashoffset: 0;
  }
  50% {
    stroke-dasharray: 90, 150;
    stroke-dashoffset: -35;
  }
  100% {
    stroke-dasharray: 90, 150;
    stroke-dashoffset: -124;
  }
}
</style>
