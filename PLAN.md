# FatRobin Redesign Plan

## Project Overview
Complete redesign of FatRobin with a new navigation-based architecture while preserving the core KMP foundation and calculation logic.

## Current State Analysis
- **Current Architecture**: Single-screen app with all inputs and results in one view
- **Calculation Engine**: Robust calculator with multiple calculation methods
- **UI**: Complex form with visual connection system showing field relationships
- **Data**: No persistence - all data lost on app restart

## New Architecture Vision

### Navigation Structure
**Bottom Navigation Bar** with 5 tabs:
- 🔍 **Search** - Search stored products/recipes, edit existing items
- ➕ **Create** - Add new products via modal bottom sheet  
- 📱 **Scan** - Barcode scanner for product lookup
- 📝 **Recipe** - Recipe builder and manager
- ⚙️ **Settings** - App configuration

### Main Screen Behavior
- **Default view**: Two lists - Favorites (user-configurable, orderable) and Recently Used
- **Item selection**: Auto-navigate to "portion mode" (main calculation screen)
- **Unsaved products**: Show save button when in portion mode with unsaved product data

### Core Design Changes

#### 1. Separation of Creation vs Calculation
- **Before**: All inputs (product data + portion calculation) in one screen
- **After**: Product/recipe creation separate from dosage calculation
- **Create Modal**: Only product/recipe definition (fat/100g, package data, food items)
- **Main Screen**: Only calculation inputs (portion weight, count, sub-package selection)

#### 2. Data Persistence
- **Before**: No storage, manual re-entry every time
- **After**: Local database storing products, recipes, and ingredients
- **Search**: Find previously entered items
- **Recent/Favorites**: Quick access to commonly used items

## Enhanced Product Creation Flow

### Product Data Fields
When saving a product, user must provide:
- **Name** (mandatory)
- **Brand** (optional)
- **Variant/Flavor** (optional)
- **Fat per 100g** (mandatory)
- **Package weight** (optional)
- **Sub-divisions** (optional)
- **Food item weight** (optional)
- **Food item count** (optional)

### Field Relationships
- Auto-calculation between package weight, food item weight, and food item count maintained
- Same logic as current implementation but moved to creation modal

## Data Verification Dialog

### When Triggered
- Upon selecting a search result (products/recipes only)
- Upon scanning and finding a barcode match
- Any time a stored product/recipe is loaded
- **Frequency limit**: Maximum once per 30 days per product, and only once per session across all products

### Dialog Content
- **Warning message**: "Please verify the loaded information as the producer might have changed composition or package size"
- **Product details**: Display all loaded data for review
- **Actions**: 
  - "Use as-is" button
  - "Edit before using" button
  - "Don't show again for 90 days" checkbox

### Behavior
- Modal dialog blocks progression until user acknowledges
- Session-based limiting prevents dialog spam during active use
- 90-day snooze option for users who want longer quiet periods

## Recipe System Enhancement

### Recipe Structure
- **Name** (mandatory)
- **Typical portion count** (how many servings the recipe makes)
- **Ingredient list** where each ingredient has:
  - Ingredient reference (from ingredient database)
  - Weight in grams for that ingredient

### Recipe Calculations
- Total recipe weight calculated from sum of ingredient weights
- Fat per 100g calculated from weighted average of ingredients
- Weight per typical portion = total weight ÷ typical portion count
- Recipes can be treated like products for calculation purposes

## Search and Edit Functionality

### Search Screen Features
- **Full fuzzy search**: Search entire dataset without pagination (Compose LazyColumn handles performance)
- **Filter tabs**: All, Products, Recipes, Ingredients
- **Search actions**: Edit, Use (navigate to portion/recipe mode), Mark as Favorite
- **Ingredient behavior**: Selecting an ingredient opens recipe creation with ingredient pre-added (weight left empty)

### Edit Capabilities
- **Products/Recipes**: Dedicated edit interface (separate from creation modal) with brand/variant/barcode fields
- **Real-time saving**: Changes saved to database immediately
- **Edit access**: Available from search results and portion mode

### Search Result Display
- **Products**: Brand, Name (Variant), Fat%, completeness indicators
- **Recipes**: Name, Portions: X, Total Weight, Fat%, completeness indicators  
- **Ingredients**: Name, Fat%, "Add to Recipe" visual cue
- **Compact layout**: Maximum information in minimal space

### Favorites Management
- **Toggle favorite**: Simple star/heart icon on any item
- **Favorites ordering**: User can manually reorder favorites list
- **Favorites display**: Prominent section on main screen, orderable by drag-and-drop
- **Unfavorite behavior**: When unchecked on main screen, item remains visible but visually marked as "pending removal" using existing Material 3 color scheme (e.g., onSurfaceVariant color, reduced alpha) with undo capability
- **Persistence**: Changes persist only when navigating away from main screen or closing app
- **Undo mechanism**: Tap on pending-removal item to restore favorite status before persistence

## Settings Screen Additions

### Settings Categories
- **Language**
  - App language selection (German, US-English initially)
  - Uses Android's per-app language system (Android 13+)
- **Pill Configuration**
  - Add/remove/edit pill strength values (default: 10,000 and 35,000 units)
  - Modify dosing factor (default: 2000 units per gram of fat)
- **Data Verification**
  - Toggle 30-day verification reminders
  - Reset 90-day snooze preferences
- **Data Management**
  - Clear all data with confirmation
  - Reset favorites and recent items
- **About/Help**
  - Version information, user guide, medical disclaimer

## Database Schema Overview

### Core Tables
- **Products**: Store all product information with brand/variant fields and barcode relationships
- **Recipes**: Store recipe metadata including portion count
- **Ingredients**: Master list with pre-defined items (milk, butter, oat flakes, flour, potatoes, tomatoes, etc.)
- **Recipe_Ingredients**: Junction table linking recipes to ingredients with weights
- **Settings**: User preferences including verification dialog settings and language
- **Favorites**: User's favorited items with custom ordering

### Key Relationships
- Recipes → Recipe_Ingredients → Ingredients (many-to-many)
- All items track creation date and last used for sorting/filtering

## Barcode Scanning Integration

### Scanning Process
- Camera view with barcode overlay for EAN/UPC recognition
- Search local database only (no internet lookup)
- **Single match**: Show verification dialog, then navigate to portion mode
- **Multiple matches**: Show variant selection UI with all product details (brand, name, variant, fat content) in compact layout
- **No match**: Option to create new product with scanned barcode pre-filled

### Barcode-Product Relationship
- Barcodes assigned only through scanning (not manual entry)
- Multiple products can share same barcode (different variants)
- Variant selection shows complete product information for informed choice

### Scanning Limitations
- Local database search only
- Manual entry fallback for unknown barcodes
- Future enhancement: Online barcode database integration

## Calculation Interface Redesign

### Main Screen Layout
- **Top section**: Currently selected product/recipe (if any)
  - Shows name, fat content, available calculation methods
  - Clear selection button
- **Middle section**: Calculation method buttons
  - "By Weight" (always available)
  - "By Count" (if food item data available)
  - "By Sub-package" (if package division data available)
- **Input section**: Changes based on selected method
  - Weight input for "by weight"
  - Count input for "by count"  
  - Sub-package count for "by sub-package"
- **Results section**: Pills needed calculation display

### Calculation Method Availability
- Determined by available data in selected product/recipe
- Buttons enabled/disabled based on data completeness
- Clear indication of why methods are unavailable

## Common Interface Design

### CalculableItem Abstraction
- Both products and recipes implement same interface
- Provides fat content, weight data, and calculation capabilities
- Enables unified handling in calculator and UI
- Methods to check which calculation types are possible

### Display Consistency
- Products show: Brand Name (Variant) - Fat%
- Recipes show: Recipe Name - Portions: X - Fat%
- Consistent card layout in all lists
- Visual distinction between products and recipes

## Migration Strategy

### Phase 1: Parallel Development
- Keep existing UI functional during development
- Build new architecture alongside existing code
- Feature flag to switch between old/new interfaces

### Phase 2: Data Migration
- Create migration scripts for any existing user data
- Ensure calculation results remain identical
- Preserve user preferences and settings

### Phase 3: Interface Transition
- Gradual rollout of new navigation structure
- User education on new workflow
- Feedback collection and refinement

## Key Principles Maintained

1. **KMP Architecture**: Core business logic remains in commonMain
2. **Modern Android Development**: Kotlin Flow, StateFlow, Jetpack Compose, AndroidViewModel with SavedStateHandle
3. **Calculation Logic**: Core calculation algorithms preserved, implementation refactored for new architecture
4. **Auto-calculation Logic**: Field interdependencies maintained
5. **Material 3 Design**: Consistent with current design system and existing color scheme
6. **Offline-first**: All functionality works without internet
7. **Data Accuracy**: Verification dialogs prevent outdated information usage

## Success Metrics

### User Experience
- Faster access to frequently used products/recipes
- Reduced data re-entry through persistence
- Enhanced discovery through search and barcode scanning
- Improved data accuracy through verification prompts

### Technical Goals
- All existing calculation methods preserved with identical results
- Refactored calculation logic with comprehensive test coverage
- Database performance suitable for local storage
- Smooth navigation and responsive UI
- Comprehensive edit capabilities for all stored data

### Data Management
- Reliable local storage of products and recipes
- Export/import capabilities for data portability
- Clear data verification workflow
- Flexible ingredient and recipe management

## Implementation Timeline

### Sprint 1-2: Foundation (Weeks 1-4)
- Database schema design and implementation
- Core data models (Product, Recipe, Ingredient)
- Navigation framework setup
- Basic CRUD operations
- Refactor calculation logic with comprehensive test coverage

### Sprint 3-4: Core Features (Weeks 5-8)
- Main calculation screen redesign with AndroidViewModel and SavedStateHandle
- Product creation modal with enhanced fields and state persistence
- Search functionality with edit capabilities and query preservation
- Data verification dialog system

### Sprint 5-6: Advanced Features (Weeks 9-12)
- Barcode scanning integration
- Recipe builder with ingredient management
- Settings screen enhancements
- Search result editing workflow

### Sprint 7-8: Polish & Migration (Weeks 13-16)
- Comprehensive testing and bug fixes
- Performance optimization
- User interface polish and animations
- Migration from old to new architecture
- Documentation and user guides

## Risk Mitigation

### Technical Risks
- Database migration complexity: Plan thorough testing with sample data
- Calculation accuracy: Comprehensive unit and integration test coverage for refactored logic
- Performance with large datasets: Monitor fuzzy search performance, implement optimizations if needed
- State management complexity: Ensure SavedStateHandle properly preserves all critical UI state

### User Experience Risks
- Workflow disruption: Gradual migration with user education
- Data loss concerns: Robust backup and export capabilities
- Feature complexity: Progressive disclosure and clear navigation

### Timeline Risks
- Scope creep: Strict prioritization of core features first
- Integration complexity: Early prototyping of critical paths
- Testing overhead: Automated testing pipeline from start

## Additional Implementation Notes

### Android State Management
- **ViewModels**: Use AndroidViewModel for all screen ViewModels to handle application context
- **State Management**: Use Kotlin Flow with StateFlow/SharedFlow for reactive state management instead of LiveData
- **State Persistence**: Implement SavedStateHandle for UI state persistence across process death and configuration changes
- **Critical State**: Ensure portion calculations, search queries, favorites pending changes, and form inputs survive app pausing/switching
- **Lifecycle Awareness**: Proper handling of app backgrounding, configuration changes, and process recreation
- **Compose Integration**: Use collectAsState() for seamless Flow integration with Compose UI

### Data Validation Rules
- **Fat per 100g**: Must be 0-100 (cannot exceed 100%)
- **Weights**: Positive numbers only, reasonable maximums (e.g., 10kg for packages)
- **Portion counts**: Positive integers only
- **Text fields**: Length limits and character validation

### Deletion Cascading Rules
- **Product deletion**: Warn if used in recipes, offer cascade delete option
- **Ingredient deletion**: Prevent if used in any recipe, show usage count
- **Recipe deletion**: Direct delete (no cascading concerns)

### Modern Android Architecture
- **Reactive Programming**: Kotlin Flow throughout the app for data streams and state management
- **Database**: SQLDelight with Flow-based queries for reactive data access (KMP compatible)
- **Repository Pattern**: Flow-based repositories with proper error handling and caching
- **UI State**: StateFlow for UI state management, SharedFlow for one-time events
- **Coroutines**: Structured concurrency with proper scoping and cancellation
- **Dependency Injection**: Hilt for dependency management across ViewModels and repositories

### Localization Requirements
- **German and US-English** initial language support
- **Measurement units**: Support for grams (primary) and ounces (conversion)
- **Currency/numbers**: Locale-appropriate formatting
- **Date formats**: Region-appropriate display
- **Android 13+ per-app language**: Proper system integration