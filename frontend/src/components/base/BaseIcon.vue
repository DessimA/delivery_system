<template>
  <component :is="iconComponent" :size="size" :stroke-width="strokeWidth" />
</template>

<script setup>
import { computed } from 'vue';
// Import all icons from lucide-vue-next
import * as LucideIcons from 'lucide-vue-next';

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

const iconComponent = computed(() => {
  if (!props.name) return null;
  // Convert kebab-case to PascalCase for icon name
  const pascalCaseName = props.name.replace(/(^\w|-\w)/g, (g) => g.toUpperCase().replace(/-/, ''));
  const icon = LucideIcons[pascalCaseName];
  if (!icon) {
    console.warn(`Icon '${props.name}' not found. Falling back to 'AlertCircle'.`);
    return LucideIcons.AlertCircle; // Fallback icon
  }
  return icon;
});
</script>

<style scoped>
/* No specific styles needed here, Lucide icons are SVG and styled via props */
</style>