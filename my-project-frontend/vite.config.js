/**
 * Vite配置文件
 * Vue项目构建工具配置，包含插件、路径别名、开发服务器等设置
 * 
 * @author 系统
 * @version 1.0
 * @since 2024
 */

// 导入Node.js路径处理模块
import { fileURLToPath, URL } from 'node:url'

// 导入Vite相关插件和配置函数
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'                    // Vue 3单文件组件支持
import vueDevTools from 'vite-plugin-vue-devtools'       // Vue开发者工具
import AutoImport from 'unplugin-auto-import/vite'      // 自动导入API
import Components from 'unplugin-vue-components/vite'   // 自动导入组件
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'  // Element Plus解析器

/**
 * Vite配置导出
 * 定义项目的构建和开发配置
 */
export default defineConfig({
  /**
   * 插件配置
   * 配置Vite构建过程中使用的各种插件
   */
  plugins: [
    // Vue 3插件，支持单文件组件
    vue(),
    
    // 自动导入插件配置
    // 自动导入Vue、Vue Router等常用API，减少手动导入
    AutoImport(
        {
            // 使用Element Plus解析器自动导入Element Plus组件
            resolvers: [ElementPlusResolver()],
        }
    ),
    
    // 自动导入组件插件配置
    // 自动导入Vue组件，无需手动导入即可使用
    Components({
      // 使用Element Plus解析器自动导入Element Plus组件
      resolvers: [ElementPlusResolver()],
    }),
  ],
  
  /**
   * 模块解析配置
   * 配置模块导入时的路径解析规则
   */
  resolve: {
    // 路径别名配置
    alias: {
      // 设置@别名指向src目录，方便模块导入
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  
  /**
   * 开发服务器配置
   * 配置开发环境下的服务器行为
   */
  server: {
    // 代理配置，解决开发环境跨域问题
    proxy: {
      // 将所有以/api开头的请求代理到后端服务器
      '/api': {
        // 后端服务器地址，端口已改为5371
        target: 'http://localhost:5371',
        // 启用跨域支持
        changeOrigin: true,
      }
    }
  }
})